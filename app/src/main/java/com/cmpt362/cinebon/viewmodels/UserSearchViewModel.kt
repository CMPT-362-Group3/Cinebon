package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.UserEntity
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserSearchViewModel: ViewModel() {
    private val userRepository: UserRepository = UserRepository.getInstance()
    private val _searchResults = MutableStateFlow<List<UserEntity>>(emptyList())
    val searchResults: StateFlow<List<UserEntity>>
        get() = _searchResults

    private var searchJob: Job? = null
    fun searchUsers(query: String) {
        searchJob = viewModelScope.launch {
            // Artificial delay to prevent too many API calls
            delay(750) // delay 750ms

            userRepository.searchUsers(query) // Dispatch the call

            searchJob = null // Reset search job reference
        }
    }

    init{
        viewModelScope.launch {
            userRepository.searchResults.collectLatest {
                _searchResults.value = it // Update search results
            }
        }
    }

    fun resetSearchResults(){
        userRepository.resetUserSearchResults() // Reset search results
    }
}
