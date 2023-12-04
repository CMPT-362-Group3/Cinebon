package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.DocumentReference

class ListEntity {

    companion object {
        const val LIST_COLLECTION = "lists"
    }

    lateinit var listId: String
    lateinit var owner: DocumentReference
    lateinit var name: String
    var movies = mutableListOf<Int>()
}

data class ResolvedListEntity (
    val listId: String,
    val owner: User,
    val name: String,
    val movies: MutableList<Movie>
)