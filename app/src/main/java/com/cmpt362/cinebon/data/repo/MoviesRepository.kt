package com.cmpt362.cinebon.data.repo

import com.cmpt362.cinebon.data.api.TMDBInstance
import com.cmpt362.cinebon.data.api.response.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MoviesRepository {
    companion object {
        val instance = MoviesRepository()
    }

    private val service = TMDBInstance.movieService

    // all of these will be fetched from the API
    private var cachedNowPlayingMovies = MutableStateFlow(emptyList<Movie>())
    private var cachedPopularMovies = MutableStateFlow(emptyList<Movie>())
    private var cachedUpcomingMovies = MutableStateFlow(emptyList<Movie>())

    // wrapper for fetch
    suspend fun getNowPlayingMovies(): StateFlow<List<Movie>> {
        if (cachedNowPlayingMovies.value.isEmpty()) {
            fetchNowPlayingMovies()
        }
        return cachedNowPlayingMovies
    }


    // returns list of movies that are now playing
    private suspend fun fetchNowPlayingMovies() {
        cachedNowPlayingMovies.value = service.getNowPlayingMovies().results
    }

    // wrapper for fetch
    suspend fun getPopularMovies(): StateFlow<List<Movie>> {
        if (cachedPopularMovies.value.isEmpty()) {
            fetchPopularMovies()
        }
        return cachedPopularMovies
    }

    // returns popular movies from api
    private suspend fun fetchPopularMovies() {
        cachedPopularMovies.value = service.getPopularMovies().results
    }

    // wrapper for fetch
    suspend fun getUpcomingMovies(): StateFlow<List<Movie>> {
        if (cachedUpcomingMovies.value.isEmpty()) {
            fetchUpcomingMovies()
        }
        return cachedUpcomingMovies
    }

    // returns upcoming movies from api
    private suspend fun fetchUpcomingMovies() {
        cachedUpcomingMovies.value = service.getUpcomingMovies().results
    }

    // get movies by id
    suspend fun getMovieById(id: Int): Movie {
        return service.getMovieById(id)
    }

    // function to search movies for search bar
    suspend fun searchMovies(query: String): List<Movie> {
        return service.searchMovies(query).results
    }
}

