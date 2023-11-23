package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.api.response.DUMMY_MOVIE_ID
import com.cmpt362.cinebon.data.api.response.DummyMovie
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.repo.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieInfoViewModel : ViewModel() {

    private val repo = MoviesRepository.instance

    val movieInfo = MutableStateFlow(Movie())

    fun getMovieInfo(id: Int) {

        if (id == DUMMY_MOVIE_ID) {
            movieInfo.value = DummyMovie
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                movieInfo.value = repo.getMovieById(id)
            }
        }
    }

}