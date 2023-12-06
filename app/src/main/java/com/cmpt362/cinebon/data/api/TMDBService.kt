package com.cmpt362.cinebon.data.api

import com.cmpt362.cinebon.BuildConfig
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.api.response.MoviesResult
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

// An interface for the TMDB API, used with retrofit
// Authentication is done in two ways:
// 1. Using a header token, stored in the local.properties file
// 2. Using an API key, given in the URL as a query parameter
// These are provided by the TMDB account. Right now, the app uses the auth values from one of our team members' accounts.
interface TMDBService {

    // Define some universal API constants
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        private const val QUERY_ADULT = "include_adult"
        private const val QUERY_LANGUAGE = "language"
        private const val QUERY_PAGE = "page"
        private const val QUERY_STRING = "query"
    }

    // API endpoint for getting "Now playing" movies from TMDB
    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(): MoviesResult

    // API endpoint for getting "Popular" movies from TMDB
    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/popular")
    suspend fun getPopularMovies(): MoviesResult

    // API endpoint for getting "Upcoming" movies from TMDB
    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(): MoviesResult

    // API endpoint for getting a movie by its ID
    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/{movie_id}")
    suspend fun getMovieById(@Path("movie_id") id: Int): Movie

    // API endpoint for searching movies by a query string
    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("search/movie?")
    suspend fun searchMovies(
        @Query(QUERY_STRING) query: String,
        @Query(QUERY_ADULT) adult: Boolean = false,
        @Query(QUERY_LANGUAGE) language: String = "en-US",
        @Query(QUERY_PAGE) page: Int = 1
    ): MoviesResult
}

// Extension functions for the TMDB API, which takes a poster path string and returns the full image URL
fun String.posterUrl() = "${TMDBService.IMAGE_BASE_URL}$this?api_key=${BuildConfig.TMDB_API_KEY}"

// Extension function for the TMDB API, which takes a runtime in minutes and returns a string in the format "Xh Ym"
fun Int.toRuntimeString(): String {
    val hours = this / 60
    val minutes = this % 60
    return "${hours}h ${minutes}m"
}