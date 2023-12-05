package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.FriendRequest
import com.cmpt362.cinebon.data.entity.ResolvedFriendRequest
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.repo.FriendsRepository
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class FriendsViewModel: ViewModel() {

    private val userRepository = UserRepository.getInstance()
    private val friendsRepository = FriendsRepository.getInstance()
    val requestList: StateFlow<List<ResolvedFriendRequest>>
        get() = friendsRepository.resolvedRequestList.map { list ->
            list.filter {
                it.receiver.userId == userRepository.userInfo.value?.userId
            }
        }


    private val _requestSent = MutableStateFlow<Boolean>(false)
    val requestSent: StateFlow<Boolean>
        get() = _requestSent

    private val _requestReceived = MutableStateFlow<Boolean>(false)
    val requestReceived: StateFlow<Boolean>
        get() = _requestReceived
    fun getRequestList() {
        viewModelScope.launch {
            friendsRepository.getRequestList()
        }
    }
    init {
        viewModelScope.launch {
            friendsRepository.requestList.collectLatest {
                friendsRepository.getResolvedRequestList()
            }
        }
    }

    fun sendRequest(user: User) {
        viewModelScope.launch {
            friendsRepository.createRequest(user)
        }
    }

    fun acceptRequest(request: FriendRequest, onResult:(Throwable?)-> Unit) {
        viewModelScope.launch {
            friendsRepository.acceptRequest(request)
        }
    }

    fun rejectRequest(request: FriendRequest) {
        viewModelScope.launch {
            friendsRepository.rejectRequest(request)
        }
    }

    fun checkRequest(user: User) {
        viewModelScope.launch{
            friendsRepository.checkRequestSent(user)
            friendsRepository.requestSent.collectLatest {
                _requestSent.value = it
            }
            friendsRepository.requestReceived.collectLatest {
                _requestReceived.value = it
            }
        }
    }

}