package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UserRepository private constructor() {

    companion object {
        const val USER_COLLECTION = "users"

        private val instance = UserRepository()

        fun getInstance(): UserRepository {
            return instance
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

    fun getUserRef(userId: String) = database.collection(USER_COLLECTION).document(userId)

    suspend fun updateCurrentUserData() {
        val docRef = getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)

        val snapShot = docRef.get().await()

        if (snapShot.exists()) {
            Log.d("UserRepository", "User data successfully retrieved")
            _userInfo.value = snapShot.toObject<User>()
        }

        Log.d("UserRepository", "Error getting user data")
    }

    suspend fun getUserData(userId: String): User? {
        val docRef = getUserRef(userId)

        val snapShot = docRef.get().await()

        if (snapShot.exists()) {
            Log.d("UserRepository", "User data successfully retrieved")
            return snapShot.toObject<User>()
        }

        Log.d("UserRepository", "Error getting user data")
        return null
    }

    suspend fun updateUserData(
        userId: String, username: String, firstName: String,
        lastName: String, email: String, onResult: (Throwable?) -> Unit
    ) {
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
                .addOnFailureListener { e ->
                    Log.w("UserRepository", "error updating user data", e)
                    onResult(e)
                }
        }
    }

    fun attachUserRefListener(listener: EventListener<DocumentSnapshot>) {
        getUserRef(FirebaseAuth.getInstance().currentUser!!.uid)
            .addSnapshotListener(listener)
    }

    fun resetUserCreatedResult() {
        _userCreatedResult.value = Result.success(false)
    }
}