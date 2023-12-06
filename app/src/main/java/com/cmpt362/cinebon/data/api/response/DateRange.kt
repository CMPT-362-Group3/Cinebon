package com.cmpt362.cinebon.data.api.response

// An immutable holder to represent a range of dates from the TMDB API
data class DateRange(
    val minimum: String,
    val maximum: String
)
