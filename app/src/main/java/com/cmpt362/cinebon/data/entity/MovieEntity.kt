package com.cmpt362.cinebon.data.entity

import kotlinx.coroutines.flow.MutableStateFlow

class MovieEntity(
    val title: String,
    val length: String,
    val language: String,
    val ageRating: String,
    val review: Int,
    val description: String,
    val releaseDate: String,
    val image: Int,
    val bookmarked: MutableStateFlow<Boolean> = MutableStateFlow(false)
)