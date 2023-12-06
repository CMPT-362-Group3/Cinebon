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

class WishlistViewModel : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    private val listRepository = ListRepository.getInstance()

    private val _wishlist = MutableStateFlow<ResolvedListEntity?>(null)
    val wishlist: StateFlow<ResolvedListEntity?>
        get() = _wishlist

    init {
        // Launch a worker to listen to the user's wishlist
        viewModelScope.launch {
            listRepository.resolvedLists.collectLatest {
                Log.d("WishlistViewModel", "Resolved lists updated")
                it.find { list -> list.listId == userRepository.userInfo.value?.defaultList?.id }?.let { wishlist ->
                    _wishlist.value = wishlist
                    Log.d("WishlistViewModel", "Wishlist updated: ${wishlist.movies.map { movie -> movie.title }}")
                }
            }
        }
    }

    fun addMovieToWishlist(movieId: Int) {
        viewModelScope.launch {
            listRepository.addMovieToList(_wishlist.value!!.listId, movieId)
        }
    }

    fun removeMovieFromWishlist(movieId: Int) {
        viewModelScope.launch {
            listRepository.deleteMovieFromList(_wishlist.value!!.listId, movieId)
        }
    }
}