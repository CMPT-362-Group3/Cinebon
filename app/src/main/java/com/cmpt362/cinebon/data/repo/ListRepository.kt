package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.entity.ListEntity
import com.cmpt362.cinebon.data.entity.ResolvedListEntity
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
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

    private val _listCreatedResult = MutableStateFlow(Result.success(false))
    val listCreatedResult: StateFlow<Result<Boolean>>
        get() = _listCreatedResult

    private val _listInfo = MutableStateFlow<ListEntity?>(null)
    val listInfo: StateFlow<ListEntity?>
        get() = _listInfo

    private val _resolvedLists = MutableStateFlow<List<ResolvedListEntity>>(emptyList())
    val resolvedLists: StateFlow<List<ResolvedListEntity>>
        get() = _resolvedLists

    private val _userLists = MutableStateFlow<List<ListEntity>>(emptyList())
    val userLists: StateFlow<List<ListEntity>>
        get() = _userLists

    suspend fun createList(list: ListEntity) {
        withContext(IO) {

            list.owner = userRepo.getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)

            database.collection(LIST_COLLECTION).add(list)
                .addOnSuccessListener {
                    Log.d("ListRepository", "List data successfully written")
                    _listCreatedResult.value = Result.success(true)
                    _listInfo.value = list
                }
                .addOnFailureListener { e ->
                    Log.w("ListRepository", "Error writing document", e)
                    _listCreatedResult.value = Result.failure(e)
                    _listInfo.value = null
                }
        }
    }

    suspend fun updateList(listId: String, updatedList: ListEntity) {
        withContext(IO) {
            database.collection(LIST_COLLECTION).document(listId)
                .set(updatedList)
                .await()
        }
    }

    suspend fun getList(listId: String): ListEntity? {
        val docRef = database.collection(LIST_COLLECTION).document(listId)

        val snapShot = docRef.get().await()

        if (snapShot.exists()) {
            Log.d("ListRepository", "List data successfully retrieved")
            return snapShot.toObject<ListEntity>()
        }

        Log.d("ListRepository", "Error getting list data")
        return null
    }

    fun resetListCreateResult() {
        _listCreatedResult.value = Result.success(false)
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
    private suspend fun resolveLists(lists: List<ListEntity>) {
        val resolvedLists = mutableListOf<ResolvedListEntity>()

        for (list in lists) {
            // Resolve the owner of the list
            val owner = userRepo.getUserData(list.owner.id) ?: continue

            // Resolve the movies of the list
            val resolvedMovies = mutableListOf<Movie>()
            list.movies.forEach { movieId ->
                movieRepo.getMovieById(movieId).let {
                    resolvedMovies.add(it)
                }
            }

            resolvedLists.add(
                ResolvedListEntity(
                    list.listId,
                    owner,
                    list.name,
                    resolvedMovies
                )
            )
        }

        _resolvedLists.value = resolvedLists
    }

    suspend fun forceResolveLists() {
        resolveLists(userLists.value)
    }

    suspend fun deleteList(listId: String) {
        withContext(IO) {
            database.collection(LIST_COLLECTION).document(listId)
                .delete()
                .await()
        }
    }
}