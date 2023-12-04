package com.cmpt362.cinebon.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel: ViewModel() {
    private val userRepository: UserRepository = UserRepository.getInstance()
    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>>
        get() = _searchResults

    fun searchUsers(query: String) {
        viewModelScope.launch() {
            userRepository.searchUsers(query)
        }
    }
    init{
        viewModelScope.launch(){
            userRepository.searchResults.collectLatest {
                _searchResults.value = it
            }
        }
    }

    fun resetSearchResults(){
        userRepository.resetUserSearchResults()
    }
}
