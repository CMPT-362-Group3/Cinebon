package com.cmpt362.cinebon.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


interface AccountService {
    fun signUp(email: String, password: String, fName: String, lName: String, onResult: (Throwable?) -> Unit)
    fun signIn(email: String, password: String, onResult: (Throwable?) -> Unit)
}

class UserAuthViewModel: ViewModel(), AccountService {
    private val auth = FirebaseAuth.getInstance()

    fun IsSignedIn(): Boolean {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            Log.d("UserAuthViewModel", "User is signed in")
            true
        } else {
            Log.d("UserAuthViewModel", "User is not signed in")
            false
        }
    }

    override fun signIn(email: String, password: String, onResult: (Throwable?) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AccountService", "User signed in successfully")
                    onResult(null)
                } else {
                    Log.d("AccountService", "User sign in failed")
                    onResult(task.exception)
                }
            }
    }

    override fun signUp(email: String, password: String, fName: String, lName: String, onResult: (Throwable?) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AccountService", "User created successfully")
                    onResult(null)
                } else {
                    Log.d("AccountService", "User creation failed")
                    onResult(task.exception)
                }
            }
    }
}