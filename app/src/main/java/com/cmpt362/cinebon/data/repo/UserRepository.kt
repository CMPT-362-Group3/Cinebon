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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                }
                .addOnFailureListener { e ->
                    Log.w("UserRepository", "Error writing document", e)
                    _userCreatedResult.value = Result.failure(e)
                }
        }
    }

    private fun getUserRef(userId: String) = database.collection(USER_COLLECTION).document(userId)

    fun getUserData(userId: String, onResult: (User?) -> Unit) {
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
                            onResult(userObj)
                        } else {
                            Log.d("UserRepository", "Failed to get profile picture")
                            onResult(null)
                        }
                    }
            } else {
                onResult(null)
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
    fun getUserChats() {
        if (_userInfo.value != null) return getUserChats(_userInfo.value!!)

        Log.d("UserRepository", "Getting user chats fresh")
        getUserData(FirebaseAuth.getInstance().currentUser!!.uid) { user ->
            getUserChats(user)
        }
    }

    // Sub-function to get chats from user object
    private fun getUserChats(user: User?) {
        if (user == null) _userChats.value = emptyList()

        Log.d("UserRepository", "Getting user chats from user object")
        val chatRefs = user!!.chats
        Log.d("UserRepository", "Chat refs: $chatRefs")
        val chats = mutableListOf<ChatEntity>()

        // TODO: Use flow instead of assigning after every chat loop
        for (chatRef in chatRefs) {
            chatRef.get().addOnSuccessListener { chat ->

                Log.d("UserRepository", "Chat gotten")

                val chatObj = chat.toObject<ChatEntity>()
                if (chatObj != null) {
                    Log.d("UserRepository", "Chat object: ${chatObj}")

                    chats.add(chatObj)
                    _userChats.value = chats
                    Log.d("UserRepository", "User chats flow updated: ${_userChats.value}")
                }
            }.addOnFailureListener { e ->
                Log.d("UserRepository", "Failed to get chat: $e")
            }
        }
    }

    fun resetUserCreatedResult() {
        _userCreatedResult.value = Result.success(false)
    }
}