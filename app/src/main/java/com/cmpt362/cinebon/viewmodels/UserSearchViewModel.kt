package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserSearchViewModel: ViewModel() {
    private val userRepository: UserRepository = UserRepository.getInstance()
    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>>
        get() = _searchResults

    private var searchJob: Job? = null
    fun searchUsers(query: String) {
        searchJob = viewModelScope.launch {
            // Artificial delay to prevent too many API calls
            delay(750)

            // Dispatch the call
            userRepository.searchUsers(query)

            // Reset search job reference
            searchJob = null
        }
    }

    init{
        viewModelScope.launch {
            userRepository.searchResults.collectLatest {
                _searchResults.value = it
            }
        }
    }

    fun resetSearchResults(){
        userRepository.resetUserSearchResults()
    }
}
