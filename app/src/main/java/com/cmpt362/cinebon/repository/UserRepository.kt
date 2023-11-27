package com.cmpt362.cinebon.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
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
    suspend fun createUserData(userId: String, username: String, fname: String, lname: String,
                       email: String) {
        withContext(IO) {
            val user = hashMapOf(
                "username" to username,
                "fname" to fname,
                "lname" to lname,
                "email" to email
            )
            database.collection(USER_COLLECTION).document(userId)
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

    fun resetUserCreatedResult() {
        _userCreatedResult.value = Result.success(false)
    }
}