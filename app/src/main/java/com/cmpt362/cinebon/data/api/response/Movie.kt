package com.cmpt362.cinebon.data.api.response

import com.google.gson.annotations.SerializedName

// Object that represents a movie from the TMDB API
// These fields are what's received in the API response JSON
data class Movie(
    val adult: Boolean = false,

    @SerializedName("backdrop_path")
    val backdrop: String = "",

    @SerializedName("genre_ids")
    val genreIds: List<Int> = emptyList(),

    val id: Int = 0,

    @SerializedName("original_language")
    val originalLanguage: String = "en",

    @SerializedName("original_title")
    val originalTitle: String = "",

    val overview: String = "",

    val popularity: Double = 0.0,

    @SerializedName("poster_path")
    val poster: String? = null,

    @SerializedName("release_date")
    val releaseDate: String = "",

    val title: String = "",

    val video: Boolean = false,

    @SerializedName("vote_average")
    val voteAverage: Double = 0.0,

    @SerializedName("vote_count")
    val voteCount: Int = 0,

    val runtime: Int = 0,
)

// Dummy movie for previews and testing
const val DUMMY_MOVIE_ID = -1
val DummyMovie = Movie(
    adult = true,
    title = "Dummy Movie",
    overview = "This is a dummy movie",
    runtime = 134,
    voteAverage = 8.5,
    voteCount = 749,
    id = DUMMY_MOVIE_ID,
    releaseDate = "2021-01-01"
)
