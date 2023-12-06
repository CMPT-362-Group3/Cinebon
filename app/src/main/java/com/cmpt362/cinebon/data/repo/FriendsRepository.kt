package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.entity.FriendRequest
import com.cmpt362.cinebon.data.entity.ResolvedFriendRequest
import com.cmpt362.cinebon.data.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val _requestList = MutableStateFlow<List<FriendRequest>>(emptyList())

    private val _resolvedRequestList = MutableStateFlow<List<ResolvedFriendRequest>>(emptyList())
    val resolvedRequestList: StateFlow<List<ResolvedFriendRequest>>
        get() = _resolvedRequestList

    private suspend fun getResolvedRequestList() {
        withContext(IO) {
            val resolvedRequestList = mutableListOf<ResolvedFriendRequest>() // Create list of requests

            _requestList.value.forEach { request -> // For each request in user's requests
                val sender = request.sender.get().await().toObject<UserEntity>() // Convert to Request object
                val receiver = request.receiver.get().await().toObject<UserEntity>() // Convert to Request object
                if (sender != null && receiver != null) {
                    resolvedRequestList.add(
                        ResolvedFriendRequest(
                            request.requestId,
                            receiver,
                            sender
                        )
                    ) // Add to list
                }
            }

            Log.d("FriendsRepository", "Resolved request list: $resolvedRequestList")
            _resolvedRequestList.value = resolvedRequestList // Set request list
        }
    }

    suspend fun getRequestList() {
        withContext(IO) {
            userRepo.userInfo.value?.let {
                val requestList = mutableListOf<FriendRequest>() // Create list of requests

                it.requests.forEach { request -> // For each request in user's requests
                    val requestSnapshot = request.get().await()
                    val requestObj = requestSnapshot.toObject<FriendRequest>() // Convert to Request object
                    if (requestObj != null) {
                        requestObj.requestId = requestSnapshot.id // Set request ID
                        requestList.add(requestObj) // Add to list
                    }
                }

                Log.d("FriendsRepository", "Request list: $requestList")
                _requestList.value = requestList // Set request list
            }
        }
    }

    suspend fun acceptRequest(friend: UserEntity) {
        withContext(IO) {
            // Update the users' friends lists
            userRepo.addFriend(friend)

            // Delete the request, it's no longer needed
            deleteRequest(friend)
        }
    }

    suspend fun deleteRequest(friend: UserEntity) {
        withContext(IO) {
            val requestRef = getRequestRef(friend)

            if (requestRef == null) {
                Log.e("FriendsRepository", "Error deleting friend request")
                return@withContext
            }

            userRepo.getUserRef(friend.userId)
                .update(REQUESTS_COLLECTION, FieldValue.arrayRemove(requestRef)) // Remove request from sender's requests
            userRepo.getUserRef(userRepo.userInfo.value!!.userId)
                .update(REQUESTS_COLLECTION, FieldValue.arrayRemove(requestRef)) // Remove request from receiver's requests

            requestRef.delete() // Delete request
        }
    }

    suspend fun createFriendRequest(receiver: UserEntity) {
        withContext(IO) {
            val request = FriendRequest() // Create new request
            request.sender = userRepo.getUserRef(auth.currentUser!!.uid) // Set sender
            request.receiver = userRepo.getUserRef(receiver.userId) // Set receiver
            val requestReference = requestsCollection.add(request).await() // Add request to collection

            userRepo.getUserRef(userRepo.userInfo.value!!.userId)
                .update(REQUESTS_COLLECTION, FieldValue.arrayUnion(requestReference))
            userRepo.getUserRef(receiver.userId)
                .update(REQUESTS_COLLECTION, FieldValue.arrayUnion(requestReference))
        }
    }

    private fun getRequestRef(friend: UserEntity): DocumentReference? {

        // Try finding it in our resolved requests
        resolvedRequestList.value.forEach {
            if (it.sender.userId == friend.userId || it.receiver.userId == friend.userId) {
                return requestsCollection.document(it.requestId)
            }
        }

        return null
    }

    suspend fun startFriendRequestRefreshWorker() {
        userRepo.userInfo.collectLatest {
            Log.d("FriendsRepository", "Starting friend request refresh worker")
            getRequestList()
        }
    }

    suspend fun startFriendRequestResolverWorker() {
        _requestList.collectLatest {
            Log.d("FriendsRepository", "Starting friend request resolver worker")
            getResolvedRequestList()
        }
    }

}