package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.objects.User

class UserEntity {
    lateinit var userId: String
    lateinit var username: String
    lateinit var fname: String
    lateinit var lname: String
    lateinit var email: String

    fun toUser(): User {
        val user = User()
        user.userId = userId
        user.username = username
        user.fname = fname
        user.lname = lname
        user.email = email
        return user
    }
}