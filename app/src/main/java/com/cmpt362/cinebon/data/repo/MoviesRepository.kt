package com.cmpt362.cinebon.data.repo

import com.cmpt362.cinebon.data.api.TMDBInstance

class MoviesRepository {

    private val service = TMDBInstance.movieService

    suspend fun getNowPlayingMovies() = service.getNowPlayingMovies()
}