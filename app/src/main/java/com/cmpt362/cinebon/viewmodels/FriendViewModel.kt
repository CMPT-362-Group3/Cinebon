package com.cmpt362.cinebon.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.enums.FriendRequestStatus
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.repo.FriendsRepository
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FriendViewModel(private val friendId: String) : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    private val friendsRepository = FriendsRepository.getInstance()

    private val _friendInfo = MutableStateFlow<User?>(null)

    private val _requestStatus = MutableStateFlow(FriendRequestStatus.NONE)
    val requestStatus: StateFlow<FriendRequestStatus>
        get() = _requestStatus

    val friendInfo: StateFlow<User?>
        get() = _friendInfo

    init {
        updateFriendDetails()

        startFriendRequestStatusWorker()
    }

    private fun startFriendRequestStatusWorker() {
        viewModelScope.launch {
            friendsRepository.resolvedRequestList.collectLatest { requests ->

                // Check if they're already friends
                if (userRepository.userInfo.value?.friends?.any { it.id == friendId } == true) {
                    Log.d("FriendViewModel", "Already friends")
                    Log.d("FriendViewModel", userRepository.userInfo.value?.friends.toString())
                    _requestStatus.value = FriendRequestStatus.ACCEPTED
                    return@collectLatest
                }

                Log.d("FriendViewModel", "Not already friends")

                val pendingRequest = requests.find {
                    (it.sender.userId == userRepository.userInfo.value?.userId && it.receiver.userId == friendId) ||
                            (it.receiver.userId == userRepository.userInfo.value?.userId && it.sender.userId == friendId)
                }

                Log.d(
                    "FriendViewModel",
                    "Pending request: R ${pendingRequest?.receiver?.userId} S ${pendingRequest?.sender?.userId}"
                )

                _requestStatus.value = when {
                    pendingRequest == null -> FriendRequestStatus.NONE
                    pendingRequest.receiver.userId == userRepository.userInfo.value?.userId -> FriendRequestStatus.RECEIVED
                    pendingRequest.sender.userId == userRepository.userInfo.value?.userId -> FriendRequestStatus.SENT
                    else -> FriendRequestStatus.ACCEPTED
                }
                Log.d("FriendViewModel", "Request status: ${_requestStatus.value}")
            }
        }
    }

    fun sendRequest(friend: User) {
        viewModelScope.launch {
            friendsRepository.createFriendRequest(friend)
        }
    }

    fun acceptRequest(friend: User) {
        viewModelScope.launch {
            friendsRepository.acceptRequest(friend)
        }
    }

    fun rejectRequest(friend: User) {
        viewModelScope.launch {
            friendsRepository.deleteRequest(friend)
        }
    }

    fun removeFriend(friend: User) {
        viewModelScope.launch {
            userRepository.removeFriend(friend)
        }
    }

    private fun updateFriendDetails() {
        viewModelScope.launch {
            _friendInfo.value = userRepository.getUserData(friendId)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FriendViewModel(id) as T
        }
    }
}