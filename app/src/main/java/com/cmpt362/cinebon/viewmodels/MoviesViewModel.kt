package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.api.response.EmptyMoviesResult
import com.cmpt362.cinebon.data.repo.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesViewModel : ViewModel() {

    private val repo = MoviesRepository.instance

    val nowPlayingMovies = MutableStateFlow(EmptyMoviesResult.results)
    val popularMovies = MutableStateFlow(EmptyMoviesResult.results)
    val upcomingMovies = MutableStateFlow(EmptyMoviesResult.results)

    init {
        getNowPlayingMovies() // Initialize the movies
        getPopularMovies() // Initialize the movies
        getUpcomingMovies() // Initialize the movies
    }

    private fun getNowPlayingMovies() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                nowPlayingMovies.value = repo.getNowPlayingMovies().value // Update the now playing movies
            }
        }
    }

    private fun getUpcomingMovies() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                upcomingMovies.value = repo.getUpcomingMovies().value // Update the upcoming movies
            }
        }
    }

    private fun getPopularMovies() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                popularMovies.value = repo.getPopularMovies().value // Update the popular movies
            }
        }
    }

}