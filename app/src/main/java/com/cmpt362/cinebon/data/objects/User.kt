package com.cmpt362.cinebon.data.objects

import android.graphics.Bitmap
import com.cmpt362.cinebon.data.entity.UserEntity

class User {
    lateinit var profilePicture: Bitmap
    lateinit var userId: String
    lateinit var username: String
    lateinit var fname: String
    lateinit var lname: String
    lateinit var email: String

    fun toEntity(): UserEntity {
        val user = UserEntity()
        user.userId = userId
        user.username = username
        user.fname = fname
        user.lname = lname
        user.email = email
        return user
    }
}