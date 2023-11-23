package com.cmpt362.cinebon.data.api

import com.cmpt362.cinebon.BuildConfig
import com.cmpt362.cinebon.data.api.response.MoviesResult
import retrofit2.http.GET
import retrofit2.http.Headers

interface TMDBService {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }

    @Headers(
        "accept: application/json",
        "Authorization: Bearer ${BuildConfig.TMDB_HEADER_TOKEN}"
    )
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(): MoviesResult
}