package com.cmpt362.cinebon.data.entity

import com.google.firebase.firestore.DocumentReference

class ListEntity {
    lateinit var owner: DocumentReference
    lateinit var listName: String
    var movies = mutableListOf<Int>()
}