package com.cmpt362.cinebon.data.objects

import com.google.firebase.firestore.DocumentReference

class User {
    lateinit var userId: String
    lateinit var username: String
    lateinit var fname: String
    lateinit var lname: String
    lateinit var email: String
    var chats = mutableListOf<DocumentReference>()
    var movieList = mutableListOf<DocumentReference>()
}