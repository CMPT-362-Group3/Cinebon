package com.cmpt362.cinebon.data.entity

import com.google.firebase.firestore.DocumentReference

class ChatEntity {
    var users = mutableListOf<DocumentReference>()
    lateinit var key: String
}