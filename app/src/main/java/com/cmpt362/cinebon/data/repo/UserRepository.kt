package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

private const val USER_COLLECTION = "users"

class UserRepository {
    private val database = Firebase.firestore

    // Success.false means initial state
    // Success.true means user created
    // Failure means userdata not created
    private val _userCreatedResult = MutableStateFlow(Result.success(false))
    val userCreatedResult: StateFlow<Result<Boolean>>
        get() = _userCreatedResult

    suspend fun createUserData(user: User) {
        withContext(IO) {
            database.collection(USER_COLLECTION).document(user.userId)
                .set(user)
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

    fun getUserData(userId: String, onResult: (User?) -> Unit) {
        val docRef = database.collection(USER_COLLECTION).document(userId)
        Log.d("UserRepository", "Getting user data")
        docRef.get().addOnSuccessListener {
            Log.d("UserRepository", "User data successfully retrieved")
            val user = it.toObject<User>()
            if (user != null) {
                onResult(user)
            } else {
                onResult(null)
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

    fun resetUserCreatedResult() {
        _userCreatedResult.value = Result.success(false)
    }
}