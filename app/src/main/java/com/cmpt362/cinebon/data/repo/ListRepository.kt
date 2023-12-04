package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.entity.ListEntity
import com.cmpt362.cinebon.data.entity.ResolvedListEntity
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
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

    private val database = Firebase.firestore

    private val _listCreatedResult = MutableStateFlow(Result.success(false))
    val listCreatedResult: StateFlow<Result<Boolean>>
        get()= _listCreatedResult

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

        if (snapShot.exists())
        {
            Log.d("ListRepository", "List data successfully retrieved")
            return snapShot.toObject<ListEntity>()
        }

        Log.d("ListRepository", "Error getting list data")
        return null
    }

    suspend fun deleteList(listId: String) {
        withContext(IO) {
            database.collection(LIST_COLLECTION).document(listId)
                .delete()
                .await()
        }
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
        val listRefs = user!!.lists
        val lists = mutableListOf<ListEntity>()

        flow {
            for (listRef in listRefs)
            {
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
            lists.add(it)

            val resolvedList = ResolvedListEntity(
                owner = user,
                listName = it.listName,
                movies = it.movies
            )
            _resolvedLists.value = _resolvedLists.value + resolvedList
        }
    }
}