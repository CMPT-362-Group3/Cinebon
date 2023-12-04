package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.DocumentReference

class ListEntity {
    lateinit var listId: String
    lateinit var owner: DocumentReference
    lateinit var listName: String
    var movies = mutableListOf<Int>()
}

data class ResolvedListEntity (
    val owner: User,
    val listName: String,
    val movies: MutableList<Movie>
)