package com.cmpt362.cinebon.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.UserEntity
import com.cmpt362.cinebon.data.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface AccountService {
    fun signUp(
        email: String, password: String, fName: String, lName: String, username: String,
        onResult: (Throwable?) -> Unit
    )

    fun signIn(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun signOut()
    fun sendResetPasswordEmail(email: String, onResult: (Throwable?) -> Unit)
    fun getSignedInUser()
    fun updateUserProfile(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        onResult: (Throwable?) -> Unit
    )
}

class UserAuthViewModel : ViewModel(), AccountService {
    private val userRepository: UserRepository = UserRepository.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var signUpJob: Job? = null
    val userFlow: StateFlow<UserEntity?>
        get() = userRepository.userInfo // Expose user data to view

    fun isSignedIn(): Boolean {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            Log.d("UserAuthViewModel", "User is signed in")
            true // User is signed in
        } else {
            Log.d("UserAuthViewModel", "User is not signed in")
            false // User is not signed in
        }
    }

    override fun signOut() {
        viewModelScope.launch {
            userRepository.signOut() // Sign out user
        }
    }

    override fun signIn(email: String, password: String, onResult: (Throwable?) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password) // Sign in user
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AccountService", "User signed in successfully")
                    onResult(null) // Continue to next step
                } else {
                    Log.d("AccountService", "User sign in failed")
                    onResult(task.exception) // Show error
                }
            }
    }

    override fun signUp(
        email: String, password: String, fName: String, lName: String,
        username: String, onResult: (Throwable?) -> Unit
    ) {
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // User create in auth
                val user = auth.currentUser

                startSignUpListener(onResult) // Start listening for user creation

                if (user != null) {
                    // Create user data in Firestore
                    viewModelScope.launch {
                        val newUser = UserEntity()

                        newUser.userId = user.uid
                        newUser.email = email
                        newUser.fname = fName
                        newUser.lname = lName
                        newUser.username = username

                        userRepository.createUserData(newUser) // Create user data in Firestore
                    }

                    user.sendEmailVerification() // Send verification email
                }
            }
            .addOnFailureListener { e ->
                Log.d("AccountService", "Failed to create user")
                onResult(e)
            }
    }

    private fun startSignUpListener(onResult: (Throwable?) -> Unit) {
        signUpJob = viewModelScope.launch {
            userRepository.userCreatedResult.collect {
                if (it.isSuccess && it.getOrNull() == true) {
                    Log.d("AccountService", "User created successfully")
                    onResult(null) // Continue to next step
                } else if (it.isFailure) {
                    Log.d("AccountService", "User creation failed")
                    onResult(Throwable()) // Show error
                }

                if ((it.isSuccess && it.getOrNull() == true) || it.isFailure) {
                    userRepository.resetUserCreatedResult() // Reset user creation result
                    signUpJob?.cancel() // Stop listening for user creation
                }
            }
        }
    }

    override fun sendResetPasswordEmail(email: String, onResult: (Throwable?) -> Unit) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email) // Send reset password email
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AccountService", "Reset password email sent successfully")
                    onResult(null) // Continue to next step
                } else {
                    Log.d("AccountService", "Reset password email failed to send")
                    onResult(task.exception) // Show error
                }
            }
    }

    override fun getSignedInUser() {
        if (userFlow.value != null) {
            return // User data already exists
        }

        try {
            auth.currentUser ?: throw Exception("User is not signed in") // Check if user is signed in

            Log.d("UserViewModel", "User is signed in")
            viewModelScope.launch { userRepository.updateCurrentUserData() } // Update user data
        } catch (e: Exception) {
            Log.d("UserViewModel", "Failed to get signed in user")
        }
    }

    override fun updateUserProfile(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        onResult: (Throwable?) -> Unit
    ) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser // get current user

        // if current user exist, launch updateUserData from userRepo to update user data
        if (firebaseUser != null) {
            if (firebaseUser.email != email) {
                firebaseUser.updateEmail(email) // update email
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("UserAuthViewModel", "User email address updated.")
                        } else {
                            Log.d("UserAuthViewModel", "Failed to update user email address.")
                            onResult(task.exception) // Show error
                        }
                    }
            }

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username) // update username
                .build()

            firebaseUser.updateProfile(profileUpdates) // update username
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            val user = userRepository.getUserData(firebaseUser.uid)
                            if (user != null) {

                                user.email = email
                                user.username = username
                                user.fname = firstName
                                user.lname = lastName

                                userRepository.updateUserData(user, onResult) // update user data
                            } else {
                                onResult(Throwable("User Not Found")) // Show error
                            }
                        }
                    } else {
                        onResult(task.exception) // Show error
                    }
                }
        } else {
            onResult(Throwable("User Not Authenticated")) // Show error
        }
    }
}