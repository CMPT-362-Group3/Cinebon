package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel: ViewModel() {
    private val userRepository: UserRepository = UserRepository.getInstance()
    private val _searchResults = MutableStateFlow<List<User>>(emptyList())//holds current search results in this viewmodel
    val searchResults: StateFlow<List<User>> //the public state flow we can observe in the UI
        get() = _searchResults

    fun searchUsers(query: String) { //starts the search for users and updates _searchResults
        viewModelScope.launch() {
            withContext(Dispatchers.IO){
                userRepository.searchResults.collect { results ->
                    _searchResults.value = results
                }
            }
        }
    }
}