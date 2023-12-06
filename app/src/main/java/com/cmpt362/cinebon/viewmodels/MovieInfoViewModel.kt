package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.repo.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieInfoViewModel(private val movieId: Int) : ViewModel() {

    private val repo = MoviesRepository.instance

    private val _movieInfo = MutableStateFlow(Movie())
    val movieInfo: StateFlow<Movie> = _movieInfo

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _movieInfo.value = repo.getMovieById(movieId)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val movieId: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovieInfoViewModel(movieId) as T
        }
    }

}