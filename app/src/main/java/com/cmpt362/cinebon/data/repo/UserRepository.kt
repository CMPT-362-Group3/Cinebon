package com.cmpt362.cinebon.data.repo

import android.graphics.BitmapFactory
import android.util.Log
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.entity.UserEntity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

private const val USER_COLLECTION = "users"

class UserRepository {
    private val database = Firebase.firestore
    private val storage = Firebase.storage

    // Success.false means initial state
    // Success.true means user created
    // Failure means userdata not created
    private val _userCreatedResult = MutableStateFlow(Result.success(false))
    val userCreatedResult: StateFlow<Result<Boolean>>
        get() = _userCreatedResult

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

    fun getUserData(userId: String, onResult: (User?) -> Unit) {
        val docRef = database.collection(USER_COLLECTION).document(userId)
        Log.d("UserRepository", "Getting user data")
        docRef.get().addOnSuccessListener {
            Log.d("UserRepository", "User data successfully retrieved")
            val user = it.toObject<UserEntity>()
            if (user != null) {
                val userObj = user.toUser()
                storage.reference.child("users/$userId/profilePhoto.jpg")
                    .getBytes(Long.MAX_VALUE).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            userObj.profilePicture = BitmapFactory
                                .decodeStream(task.result.inputStream())

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

    suspend fun updateUserData(userId: String, username: String, firstName: String, lastName: String, email: String) {
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
                }
                .addOnFailureListener{ e ->
                    Log.w("UserRepository", "error updating user data", e)
                    throw e
                }
        }
    }

    fun resetUserCreatedResult() {
        _userCreatedResult.value = Result.success(false)
    }
}