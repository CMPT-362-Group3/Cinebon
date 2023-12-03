package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.DocumentReference

class UserEntity {
    lateinit var userId: String
    lateinit var username: String
    lateinit var fname: String
    lateinit var lname: String
    lateinit var email: String
    var chats = mutableListOf<DocumentReference>()
    var friends = mutableListOf<DocumentReference>()

    fun toUser(): User {
        val user = User()
        user.userId = userId
        user.username = username
        user.fname = fname
        user.lname = lname
        user.email = email
        user.chats = chats
        return user
    }
}