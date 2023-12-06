package com.cmpt362.cinebon.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.ResolvedListEntity
import com.cmpt362.cinebon.data.repo.ListRepository
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WatchlistViewModel : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    private val listRepository = ListRepository.getInstance()

    private val _watchlist = MutableStateFlow<ResolvedListEntity?>(null)
    val watchlist: StateFlow<ResolvedListEntity?>
        get() = _watchlist

    init {
        // Launch a worker to listen to the user's watchlist
        viewModelScope.launch {
            listRepository.resolvedLists.collectLatest {
                Log.d("WatchlistViewModel", "Resolved lists updated")
                it.find { list -> list.listId == userRepository.userInfo.value?.defaultList?.id }?.let { watchlist ->
                    _watchlist.value = watchlist // Update the watchlist
                    Log.d("WatchlistViewModel", "Watchlist updated: ${watchlist.movies.map { movie -> movie.title }}")
                }
            }
        }
    }

    fun addMovieToWatchlist(movieId: Int) {
        viewModelScope.launch {
            if (_watchlist.value == null)
                return@launch
            listRepository.addMovieToList(_watchlist.value!!.listId, movieId) // Add the movie to the watchlist
        }
    }

    fun removeMovieFromWatchlist(movieId: Int) {
        viewModelScope.launch {
            listRepository.deleteMovieFromList(_watchlist.value!!.listId, movieId) // Remove the movie from the watchlist
        }
    }
}