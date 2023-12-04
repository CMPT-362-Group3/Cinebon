package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.entity.ListEntity
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val database = Firebase.firestore

    private val _listCreatedResult = MutableStateFlow(Result.success(false))
    val listCreatedResult: StateFlow<Result<Boolean>>
        get()= _listCreatedResult

    private val _listInfo = MutableStateFlow<ListEntity?>(null)
    val listInfo: StateFlow<ListEntity?>
        get() = _listInfo

    suspend fun createList(list: ListEntity) {
        withContext(IO) {
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
}