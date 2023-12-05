package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.entity.Request
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FriendsRepository private constructor() {

    companion object {
        const val REQUESTS_COLLECTION = "requests"
        private val instance = FriendsRepository()

        fun getInstance(): FriendsRepository {
            return instance
        }
    }
    private val userRepo = UserRepository.getInstance()
    private val database = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val requestsCollection: CollectionReference = database.collection(REQUESTS_COLLECTION)

    private val _requestList = MutableStateFlow<List<Request>>(emptyList())
    val requestList: StateFlow<List<Request>>
        get() = _requestList

    private val _requestSent = MutableStateFlow<Boolean>(false)
    val requestSent: StateFlow<Boolean>
        get() = _requestSent

    private val _requestReceived = MutableStateFlow<Boolean>(false)
    val requestReceived: StateFlow<Boolean>
        get() = _requestReceived

    suspend fun getRequestList(){
        withContext(IO) {
            try {
                val querySnapshot = requestsCollection
                    .whereEqualTo("receiver", auth.currentUser!!.uid)
                    .get()
                    .await()

                val requests = querySnapshot.documents.mapNotNull { it.toObject<Request>().apply {
                    if (this != null) {
                        this.requestId = it.id //applying document snapshot id as request id
                    }
                } }

                _requestList.value = requests
                Log.d("FriendsRepository", "$requests")
            } catch (e: Exception) {
                Log.w("FriendsRepository", "error getting list of requests", e)
                _requestList.value = emptyList()
            }
        }
    }

    suspend fun acceptRequest(request: Request) {
        withContext(IO){
            requestsCollection
                .document(request.requestId)
                .update("accepted", true)
                .addOnSuccessListener {
                    Log.d("FriendsRepository", "request status updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("FriendsRepository", "error updating request status", e)
                }
            userRepo.addFriends(request.receiver, request.sender)
            userRepo.addFriends(request.sender, request.receiver)
        }

    }

    suspend fun rejectRequest(request: Request, onResult: (Throwable?) -> Unit) {
        withContext(IO){
            requestsCollection
                .document(request.requestId)
                .delete()
                .addOnSuccessListener {
                    Log.d("FriendsRepository", "friend request deleted successfully")
                    onResult(null)
                }
                .addOnFailureListener { e ->
                    Log.w("FriendsRepository", "error deleting friend request", e)
                    onResult(e)
                }
        }

    }

    suspend fun createRequest(receiver: User){
        withContext(IO){
            val request = Request()
            request.sender = auth.currentUser!!.uid
            request.receiver = receiver.userId
            requestsCollection.add(request)
        }
    }

    suspend fun checkRequestSent(user:User){
        withContext(IO){
            try {
                val querySnapshot = requestsCollection
                    .whereEqualTo("sender", auth.currentUser!!.uid)
                    .whereEqualTo("receiver", user.userId)
                    .get()
                    .await()
                if(!querySnapshot.isEmpty){
                    _requestSent.value = true
                } else {
                    _requestSent.value = false
                    Log.d("FriendsRepository", "Friend request not sent")
                }

                val querySnapshot2 = requestsCollection
                    .whereEqualTo("sender", user.userId)
                    .whereEqualTo("receiver", auth.currentUser!!.uid)
                    .get()
                    .await()
                if(!querySnapshot2.isEmpty){
                    _requestReceived.value = true
                } else {
                    _requestReceived.value = false
                    Log.d("FriendsRepository", "Friend request not sent")
                }

            } catch (e: Exception) {
                Log.e("FriendsRepository", "Error checking friend request", e)
            }
        }
    }

}