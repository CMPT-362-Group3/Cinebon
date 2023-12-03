package com.cmpt362.cinebon.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface AccountService {
    fun signUp(
        email: String, password: String, fName: String, lName: String, username: String,
        profilePhoto: Bitmap, onResult: (Throwable?) -> Unit
    )

    fun signIn(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun sendResetPasswordEmail(email: String, onResult: (Throwable?) -> Unit)
    fun getSignedInUser(onResult: (User?) -> Unit)
}

class UserAuthViewModel(private val userRepository: UserRepository = UserRepository.getInstance()) : ViewModel(),
    AccountService {
    private val auth = FirebaseAuth.getInstance()
    private var signUpJob: Job? = null
    val userFlow: StateFlow<User?>
        get() = userRepository.userInfo

    fun isSignedIn(): Boolean {
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

    override fun signUp(
        email: String, password: String, fName: String, lName: String,
        username: String, profilePhoto: Bitmap, onResult: (Throwable?) -> Unit
    ) {
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // User create in auth
                val user = auth.currentUser

                startSignUpListener(onResult)

                if (user != null) {
                    // Create user data in Firestore
                    viewModelScope.launch {
                        val newUser = User()
                        newUser.userId = user.uid
                        newUser.email = email
                        newUser.fname = fName
                        newUser.lname = lName
                        newUser.username = username
//                        newUser.profilePicture = profilePhoto

                        userRepository
                            .createUserData(newUser)
                    }

                    // Send verification email
                    user.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("AccountService", "Verification email sent successfully")
                            } else {
                                Log.d("AccountService", "Verification email failed to send")
                            }
                        }
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

                if ((it.isSuccess && it.getOrNull() == true) ||
                    it.isFailure
                ) {
                    userRepository.resetUserCreatedResult()
                    signUpJob?.cancel()
                }
            }
        }
    }

    override fun sendResetPasswordEmail(email: String, onResult: (Throwable?) -> Unit) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AccountService", "Reset password email sent successfully")
                    onResult(null)
                } else {
                    Log.d("AccountService", "Reset password email failed to send")
                    onResult(task.exception)
                }
            }
    }

    override fun getSignedInUser(onResult: (User?) -> Unit) {
        if (userFlow.value != null) {
            return
        }
        try {
            val authUser = auth.currentUser ?: throw Exception("User is not signed in")
            Log.d("UserViewModel", "User is signed in")
//            userRepository.getUserData(authUser.uid, authUser.photoUrl ?: Uri.EMPTY, onResult)
            userRepository.getUserData(authUser.uid)
        } catch (e: Exception) {
            Log.d("UserViewModel", "Failed to get signed in user")
        }
    }
}