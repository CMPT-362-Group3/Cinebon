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

    private val repo = MoviesRepository()

    val nowPlayingMovies = MutableStateFlow(EmptyMoviesResult)

    init {
        fetchNowPlayingMovies()
    }

    private fun fetchNowPlayingMovies() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                nowPlayingMovies.value = repo.getNowPlayingMovies()
            }
        }
    }

}