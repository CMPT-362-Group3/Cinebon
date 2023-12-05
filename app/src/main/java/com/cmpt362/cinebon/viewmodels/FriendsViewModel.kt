package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.Request
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.repo.FriendsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FriendsViewModel: ViewModel() {

    private val friendsRepository = FriendsRepository.getInstance()
    private val _requestList = MutableStateFlow<List<Request>>(emptyList())
    val requestList: StateFlow<List<Request>>
        get() = _requestList

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
    init{
        viewModelScope.launch {
            friendsRepository.requestList.collectLatest {
                _requestList.value = it
            }
        }
    }

    fun sendRequest(user: User) {
        viewModelScope.launch {
            friendsRepository.createRequest(user)
        }
    }

    fun acceptRequest(request: Request, onResult:(Throwable?)-> Unit) {
        viewModelScope.launch {
            friendsRepository.acceptRequest(request)
        }
    }

    fun rejectRequest(request: Request, onResult:(Throwable?)-> Unit) {
        viewModelScope.launch {
            friendsRepository.rejectRequest(request, onResult)
        }
    }

    fun checkRequest(user: User){
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