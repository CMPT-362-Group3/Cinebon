package com.cmpt362.cinebon.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendViewModel(userId: String): ViewModel() {
    private val userRepository = UserRepository.getInstance()

    private val _friendInfo = MutableStateFlow<User?>(null)
    val friendInfo: StateFlow<User?>
        get() = _friendInfo
    init {
        viewModelScope.launch {
            _friendInfo.value = userRepository.getUserData(userId)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FriendViewModel(id) as T
        }
    }
}