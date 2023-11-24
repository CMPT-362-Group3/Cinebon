package com.cmpt362.cinebon.viewmodels

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class UserAuthViewModel: ViewModel(), DefaultLifecycleObserver {
    private lateinit var auth : FirebaseAuth
    var idToken = MutableLiveData<String?>(null)
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("UserAuthViewModel", "User is signed in")
            currentUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        idToken.value = task.result?.token
                    }
                }
        } else {
            Log.d("UserAuthViewModel", "User is not signed in")
        }
    }

    fun signUp(email: String, password: String, fName: String, lName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("UserAuthViewModel", "User created successfully")
                    val user = auth.currentUser
                    if (user != null) {
                        Log.d("UserAuthViewModel", "User is signed in")
                        Log.d("UserAuthViewModel", "User email is ${user.email}")
                        Log.d("UserAuthViewModel", "User uid is ${user.uid}")
                        user.getIdToken(true)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    idToken.value = task.result?.token
                                } else  {
                                    Log.d("UserAuthViewModel", "User token retrieval failed")
                                }
                            }

                    } else {
                        Log.d("UserAuthViewModel", "User is not signed in")
                    }
                } else {
                    Log.d("UserAuthViewModel", "User creation failed")
                }
            }
    }
}

@Composable
fun <viewModel : LifecycleObserver> viewModel.ObserveLifecycleEvents(lifecycle: Lifecycle) {
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(this@ObserveLifecycleEvents)
        onDispose {
            lifecycle.removeObserver(this@ObserveLifecycleEvents)
        }
    }
}