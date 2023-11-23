package com.cmpt362.cinebon.data.api

import com.cmpt362.cinebon.BuildConfig
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.api.response.MoviesResult
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface TMDBService {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }

    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(): MoviesResult

    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/popular")
    suspend fun getPopularMovies(): MoviesResult

    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(): MoviesResult

    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/{movie_id}")
    suspend fun getMovieById(@Path("movie_id") id: Int): Movie
}

fun String.posterUrl() = "${TMDBService.IMAGE_BASE_URL}$this?api_key=${BuildConfig.TMDB_API_KEY}"
fun Boolean.toPGString() = if (this) "18+" else "Family Friendly"

fun Int.toRuntimeString(): String {
    val hours = this / 60
    val minutes = this % 60
    return "${hours}h ${minutes}m"
}