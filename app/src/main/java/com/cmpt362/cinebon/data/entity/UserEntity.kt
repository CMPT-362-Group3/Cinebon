package com.cmpt362.cinebon.data.entity

import com.google.firebase.firestore.DocumentReference

class UserEntity {
    lateinit var userId: String
    lateinit var username: String
    lateinit var fname: String
    lateinit var lname: String
    lateinit var email: String
    lateinit var defaultList: DocumentReference
    var chats = mutableListOf<DocumentReference>()
    var movieList = mutableListOf<DocumentReference>()
    var friends = mutableListOf<DocumentReference>()
    var requests = mutableListOf<DocumentReference>()
}