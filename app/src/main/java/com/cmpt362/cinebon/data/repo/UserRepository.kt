package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.entity.ChatEntity
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UserRepository private constructor() {

    companion object {
        const val USER_COLLECTION = "users"

        fun getInstance(): UserRepository {
            return UserRepository()
        }
    }

    private val database = Firebase.firestore

    // Success.false means initial state
    // Success.true means user created
    // Failure means userdata not created
    private val _userCreatedResult = MutableStateFlow(Result.success(false))
    val userCreatedResult: StateFlow<Result<Boolean>>
        get() = _userCreatedResult

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?>
        get() = _userInfo

    suspend fun createUserData(user: User) {
        withContext(IO) {
            database.collection(USER_COLLECTION).document(user.userId)
                .set(user)
                .addOnSuccessListener {
                    Log.d("UserRepository", "User data successfully written")
                    _userCreatedResult.value = Result.success(true)
                    _userInfo.value = user
                }
                .addOnFailureListener { e ->
                    Log.w("UserRepository", "Error writing document", e)
                    _userCreatedResult.value = Result.failure(e)
                    _userInfo.value = null
                }
        }
    }

    suspend fun signOut() {
        withContext(IO) {
            FirebaseAuth.getInstance().signOut()
            _userInfo.value = null
        }
    }

    private fun getUserRef(userId: String) = database.collection(USER_COLLECTION).document(userId)

    fun getUserData(userId: String) {
        val docRef = getUserRef(userId)

        Log.d("UserRepository", "Getting user data")

        docRef.get().addOnSuccessListener {

            Log.d("UserRepository", "User data successfully retrieved")
            val user = it.toObject<User>()
            if (user != null) {
                _userInfo.value = user
            } else {
                _userInfo.value = null
            }
        }
    }

    suspend fun updateUserData(userId: String, username: String, firstName: String,
                               lastName: String, email: String, onResult: (Throwable?) -> Unit) {
        withContext(IO) {
            database.collection(USER_COLLECTION).document(userId)
                .update(
                    "username", username,
                    "fname", firstName,
                    "lname", lastName,
                    "email", email
                )
                .addOnSuccessListener {
                    Log.d("UserRepository", "user data updated successfully")
                    onResult(null)
                }
                .addOnFailureListener{ e ->
                    Log.w("UserRepository", "error updating user data", e)
                    onResult(e)
                }
        }
    }

    fun attachUserRefListener(listener: EventListener<DocumentSnapshot>) {
        getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)
            .addSnapshotListener(listener)
    }

    private val _userChats = MutableStateFlow<List<ChatEntity>>(emptyList())
    val userChats: StateFlow<List<ChatEntity>>
        get() = _userChats

    // Get chats from last known user info, or fetch user and then get chats
    private var chatJob: Job? = null
    suspend fun updateUserChats() {
        // If we're already waiting for a chat update, don't trigger another.
        if (chatJob != null) return

        // If there's no chat update but we already have user info
        if (_userInfo.value != null) return updateUserChats(_userInfo.value!!)

        // We don't have user info, and we're not waiting for a chat update
        // Listen to the user info stateflow and update chats when it changes
        // Track it in a job and cancel once non-null and complete.
        // We don't request a user data fetch because if we didn't already trigger it earlier,
        // we have bigger issues than not having chats.
        Log.d("UserRepository", "Getting user chats fresh")
        coroutineScope {
            chatJob = launch {
                _userInfo.collect {
                    if (it != null) {
                        updateUserChats(it)
                        chatJob?.cancel()
                    }
                }
            }
        }
    }

    // Sub-function to get chats from user object
    private suspend fun updateUserChats(user: User?) {
        if (user == null) _userChats.value = emptyList()

        Log.d("UserRepository", "Getting user chats from user object")
        val chatRefs = user!!.chats
        Log.d("UserRepository", "Chat refs: $chatRefs")
        val chats = mutableListOf<ChatEntity>()

        flow {
            for (chatRef in chatRefs) {
                chatRef.get().await().toObject<ChatEntity>()?.let { emit(it) }
            }
        }.onCompletion {
            _userChats.value = chats
        }.collect {
            chats.add(it)
        }
    }

    fun resetUserCreatedResult() {
        _userCreatedResult.value = Result.success(false)
    }
}