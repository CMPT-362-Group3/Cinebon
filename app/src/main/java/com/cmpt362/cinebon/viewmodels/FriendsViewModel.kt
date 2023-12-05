package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.ResolvedFriendRequest
import com.cmpt362.cinebon.data.repo.FriendsRepository
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FriendsViewModel : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    private val friendsRepository = FriendsRepository.getInstance()

    private val _receivedRequests = MutableStateFlow<List<ResolvedFriendRequest>>(emptyList())
    val receivedRequests: StateFlow<List<ResolvedFriendRequest>> = _receivedRequests

    fun getRequestList() {
        viewModelScope.launch {
            friendsRepository.getRequestList()
        }
    }

    init {
        // Launch a worker to filter out received requests when resolved
        viewModelScope.launch {
            friendsRepository.resolvedRequestList.collectLatest { reqList ->
                _receivedRequests.value = reqList.filter { it.sender != userRepository.userInfo.value }
            }
        }
    }
}