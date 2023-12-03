package com.cmpt362.cinebon.data.repo

import android.graphics.BitmapFactory
import android.util.Log
import com.cmpt362.cinebon.data.entity.ChatEntity
import com.cmpt362.cinebon.data.entity.UserEntity
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    private val storage = Firebase.storage

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
            /*val bitmap = user.profilePicture
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            storage.reference.child("users/${user.userId}/profilePhoto.jpg")
                .putBytes(baos.toByteArray())
                .addOnSuccessListener {
                    Log.d("UserRepository", "Profile picture uploaded")
                }
                .addOnFailureListener { e ->
                    Log.d("UserRepository", "Profile picture upload failed")
                }*/
            // ^ This was my attempt to upload the profile picture to firebase storage
            // I was unable to get it to work, so I commented it out
            // I also commented out the code in UserAuthViewModel.kt that calls this function
            // https://firebase.google.com/docs/storage/android/upload-files
            // Check this link for more info on how to upload files to firebase storage


            database.collection(USER_COLLECTION).document(user.userId)
                .set(user.toEntity())
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

    private fun getUserRef(userId: String) = database.collection(USER_COLLECTION).document(userId)

    fun getUserData(userId: String) {
        val docRef = getUserRef(userId)

        Log.d("UserRepository", "Getting user data")

        docRef.get().addOnSuccessListener {

            Log.d("UserRepository", "User data successfully retrieved")

            val user = it.toObject<UserEntity>()
            if (user != null) {
                val userObj = user.toUser()
                Log.d("UserRepository", "User data successfully converted: ${user.chats}")

                storage.reference.child("users/$userId/profilePhoto.jpg")
                    .getBytes(Long.MAX_VALUE).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            userObj.profilePicture = BitmapFactory
                                .decodeStream(task.result.inputStream())

                            _userInfo.value = userObj
                            Log.d("UserRepository", "Successfully tracking user info: ${_userInfo.value?.username}")
                        } else {
                            Log.d("UserRepository", "Failed to get profile picture")
                            _userInfo.value = null
                        }
                    }
            } else {
                _userInfo.value = null
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

        // TODO: Use flow instead of assigning after every chat loop
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