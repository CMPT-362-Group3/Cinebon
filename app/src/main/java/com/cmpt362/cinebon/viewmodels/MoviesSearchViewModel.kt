package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.repo.MoviesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MoviesSearchViewModel : ViewModel() {

    private val movieRepo = MoviesRepository.instance

    private val _searchResults = MutableStateFlow(emptyList<Movie>())
    val searchResults: StateFlow<List<Movie>> = _searchResults

    private var searchJob: Job? = null

    // Method to update search results for a given input query
    // It dispatches a search requests after an input delay
    // The delay allows us to cancel any previously sent requests for more efficiency
    fun updateSearchResults(query: String, rateLimit: Boolean = true) {
        searchJob?.cancel()

        // If there's nothing to search, reset the results
        if (query.trim().isEmpty()) {
            return resetSearchResults()
        }

        searchJob = viewModelScope.launch {
            // Artificial 1-second input delay for cancellation
            if (rateLimit) delay(750)

            // Dispatch the get request
            _searchResults.value = movieRepo.searchMovies(query.trim())

            // On completion, reset the job
            searchJob = null
        }
    }

    fun resetSearchResults() {
        _searchResults.value = emptyList()
    }

}