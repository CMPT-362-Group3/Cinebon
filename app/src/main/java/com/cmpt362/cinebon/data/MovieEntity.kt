package com.cmpt362.cinebon.data

data class MovieEntity(
    val title: String,
    val length: String,
    val language: String,
    val ageRating: String,
    val review: Int,
    val description: String,
    val releaseDate: String
)