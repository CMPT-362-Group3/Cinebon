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

    private var cachedNowPlayingMovies = MutableStateFlow(emptyList<Movie>())
    private var cachedPopularMovies = MutableStateFlow(emptyList<Movie>())
    private var cachedUpcomingMovies = MutableStateFlow(emptyList<Movie>())

    // TODO: Modify this function to cache and return cache before fetching from API
    // TODO: Cache should store timestamp and application should check if cache is stale
    // TODO: Cache stale time should probably be a few minutes at least
    // TODO: When cache IS stale, we still return cache but ALSO trigger the api request
    // TODO: when the api request returns, we update the cache and notify the UI that the cache has been updated somehow
    suspend fun getNowPlayingMovies(): StateFlow<List<Movie>> {
        if (cachedNowPlayingMovies.value.isEmpty()) {
            fetchNowPlayingMovies()
        }
        return cachedNowPlayingMovies
    }

    private suspend fun fetchNowPlayingMovies() {
        cachedNowPlayingMovies.value = service.getNowPlayingMovies().results
    }

    suspend fun getPopularMovies(): StateFlow<List<Movie>> {
        if (cachedPopularMovies.value.isEmpty()) {
            fetchPopularMovies()
        }
        return cachedPopularMovies
    }

    private suspend fun fetchPopularMovies() {
        cachedPopularMovies.value = service.getPopularMovies().results
    }

    suspend fun getUpcomingMovies(): StateFlow<List<Movie>> {
        if (cachedUpcomingMovies.value.isEmpty()) {
            fetchUpcomingMovies()
        }
        return cachedUpcomingMovies
    }

    private suspend fun fetchUpcomingMovies() {
        cachedUpcomingMovies.value = service.getUpcomingMovies().results
    }

    suspend fun getMovieById(id: Int): Movie {
        return service.getMovieById(id)
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return service.searchMovies(query).results
    }
}

