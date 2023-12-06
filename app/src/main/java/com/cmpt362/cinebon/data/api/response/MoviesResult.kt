package com.cmpt362.cinebon.data.api.response

import com.google.gson.annotations.SerializedName

// Object to hold the results of a list of movies query
data class MoviesResult(

    val dates: DateRange,
    val page: Int,
    val results: List<Movie>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int,
)

// Dummy object to use for previews and testing
val EmptyMoviesResult = MoviesResult(
    dates = DateRange("", ""),
    page = 1,
    results = emptyList(),
    totalPages = 1,
    totalResults = 0
)