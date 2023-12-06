package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.entity.ListEntity
import com.cmpt362.cinebon.data.entity.ResolvedListEntity
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ListRepository private constructor() {

    companion object {
        const val LIST_COLLECTION = "lists"

        private val instance = ListRepository()

        fun getInstance(): ListRepository {
            return instance
        }
    }

    private val userRepo = UserRepository.getInstance()
    private val movieRepo = MoviesRepository.instance

    private val database = Firebase.firestore

    private val _resolvedLists = MutableStateFlow<List<ResolvedListEntity>>(emptyList())
    val resolvedLists: StateFlow<List<ResolvedListEntity>>
        get() = _resolvedLists

    private val _userLists = MutableStateFlow<List<ListEntity>>(emptyList())
    val userLists: StateFlow<List<ListEntity>>
        get() = _userLists

    private suspend fun createList(list: ListEntity, isDefault: Boolean = false) {
        withContext(IO) {

            list.owner = userRepo.getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)

            val listRef = database.collection(LIST_COLLECTION).add(list)
                .addOnFailureListener { e ->
                    Log.w("ListRepository", "Error writing document", e)
                }
                .await()

            Log.d("ListRepository", "List data successfully written")

            // Add the new list doc to the user's subscribed lists
            // This should update the user snapshot, which will refresh everything else automatically!
            userRepo.addUserList(listRef, isDefault)
        }
    }

    suspend fun createDefaultList() {
        createList(
            ListEntity().apply {
                owner = userRepo.getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)
                userRepo.getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)
                name = "Watchlist"
            },
            isDefault = true
        )
    }

    suspend fun createEmptyNewList() {
        createList(
            ListEntity().apply {
                owner = userRepo.getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)
                userRepo.getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)
                name = "New List"
            }
        )
    }

    suspend fun updateList(listId: String, updatedList: ListEntity) {
        withContext(IO) {
            database.collection(LIST_COLLECTION).document(listId)
                .set(updatedList)
                .await()
        }
    }

    suspend fun updateListName(listId: String, name: String) {
        withContext(IO) {
            database.collection(LIST_COLLECTION).document(listId)
                .update("name", name)
        }
    }

    suspend fun addMovieToList(listId: String, movieId: Int) {
        withContext(IO) {
            database.collection(LIST_COLLECTION).document(listId)
                .update("movies", FieldValue.arrayUnion(movieId))
        }
    }

    suspend fun deleteMovieFromList(listId: String, movieId: Int) {
        withContext(IO) {
            database.collection(LIST_COLLECTION).document(listId)
                .update("movies", FieldValue.arrayRemove(movieId))
        }
    }

    // Private method to fetch list data from remote
    private suspend fun getListRemote(listId: String): ListEntity? {
        val docRef = database.collection(LIST_COLLECTION).document(listId)

        val snapShot = docRef.get().await()

        if (snapShot.exists()) {
            Log.d("ListRepository", "List data successfully retrieved")
            return snapShot.toObject<ListEntity>()
        }

        Log.d("ListRepository", "Error getting list data")
        return null
    }

    // Check the locally fetched list for a match, if not found, fetch from remote
    fun getResolvedListById(listId: String): ResolvedListEntity? {
        return _resolvedLists.value.find { list -> list.listId == listId }
    }

    suspend fun getResolvedExternalListById(listId: String): ResolvedListEntity? {
        val listRef = database.collection(LIST_COLLECTION).document(listId)
        return getResolvedList(listRef.get().await().toObject<ListEntity>())
    }

    private suspend fun getResolvedList(listEntity: ListEntity?): ResolvedListEntity? {
        if (listEntity == null) return null
        val owner = userRepo.getUserData(listEntity.owner.id) ?: return null

        val resolvedMovies = mutableListOf<Movie>()
        listEntity.movies.forEach { movieId ->
            movieRepo.getMovieById(movieId).let {
                resolvedMovies.add(it)
            }
        }

        return ResolvedListEntity(
            listEntity.listId,
            owner,
            listEntity.name,
            resolvedMovies,
            owner.userId == FirebaseAuth.getInstance().currentUser!!.uid
        )
    }

    suspend fun startListRefreshWorker() {
        userRepo.userInfo.collectLatest {
            if (it == null) return@collectLatest

            Log.d("ListRepository", "User info updated, updating lists")
            updateUserLists(it)
        }
    }

    private suspend fun updateUserLists(user: User?) {
        if (user == null) _userLists.value = emptyList()

        Log.d("ListRepository", "Getting user lists from user object")
        val listRefs = user!!.movieList
        val lists = mutableListOf<ListEntity>()

        flow {
            for (listRef in listRefs) {
                listRef.get().await().apply {
                    toObject<ListEntity>()?.let {
                        it.listId = this.id
                        emit(it)
                    }
                }
            }
        }.onCompletion {
            Log.d("ListRepository", "User lists updated with size ${lists.size}")
            _userLists.value = lists
        }.collect {
            Log.d("ListRepository", "User list collected with id ${it.listId}")
            lists.add(it)
        }
    }

    // Method to attach listeners to list document
    // All the registered listeners are tracked by the repository
    // And can be requested to be removed.
    private val _listRefsListeners = mutableListOf<ListenerRegistration>()
    fun attachListRefListener(list: ListEntity, listener: EventListener<DocumentSnapshot>) {
        val listRef = database.collection(LIST_COLLECTION).document(list.listId)

        // Add the listener and track it
        listRef.addSnapshotListener(listener).let {
            _listRefsListeners.add(it)
        }
    }

    // This method will remove all the list update listeners that were registered
    fun invalidateListRefsListeners() {
        _listRefsListeners.forEach {
            it.remove()
        }

        _listRefsListeners.clear()
    }

    suspend fun attachListResolverWorker() {
        userLists.collectLatest {
            resolveLists(it)
        }
    }

    // Method to convert ListEntity to ResolvedListEntity for each list
    private suspend fun resolveLists(lists: List<ListEntity>, fetchSource: Boolean = false) {
        val resolvedLists = mutableListOf<ResolvedListEntity>()

        for (list in lists) {
            // Check if we're forcing re-fetch
            val actualList = if (fetchSource) getListRemote(list.listId) ?: list else list

            // Resolve the owner of the list
            val owner = userRepo.getUserData(actualList.owner.id) ?: continue

            // Resolve the movies of the list
            val resolvedMovies = mutableListOf<Movie>()
            actualList.movies.forEach { movieId ->
                movieRepo.getMovieById(movieId).let {
                    resolvedMovies.add(it)
                }
            }

            resolvedLists.add(
                ResolvedListEntity(
                    list.listId,
                    owner,
                    actualList.name,
                    resolvedMovies,
                    owner.userId == FirebaseAuth.getInstance().currentUser!!.uid
                )
            )
        }

        _resolvedLists.value = resolvedLists
    }

    suspend fun forceUpdateLists() {
        Log.d("ListRepository", "Forcing list resolution")
        resolveLists(_userLists.value, fetchSource = true)
    }

    suspend fun deleteList(listId: String) {
        withContext(IO) {
            database.collection(LIST_COLLECTION).document(listId)
                .delete()
                .await()
        }
    }
}