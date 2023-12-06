package com.cmpt362.cinebon.data.api

import com.cmpt362.cinebon.BuildConfig
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.api.response.MoviesResult
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        private const val QUERY_ADULT = "include_adult"
        private const val QUERY_LANGUAGE = "language"
        private const val QUERY_PAGE = "page"
        private const val QUERY_STRING = "query"
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

fun String.posterUrl() = "${TMDBService.IMAGE_BASE_URL}$this?api_key=${BuildConfig.TMDB_API_KEY}"

fun Int.toRuntimeString(): String {
    val hours = this / 60
    val minutes = this % 60
    return "${hours}h ${minutes}m"
}