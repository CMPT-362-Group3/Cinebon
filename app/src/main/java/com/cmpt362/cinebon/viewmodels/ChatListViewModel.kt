package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.ResolvedChatEntity
import com.cmpt362.cinebon.data.repo.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {

    private val chatRepository = ChatRepository.getInstance()

    val resolvedChats = chatRepository.resolvedChats

    private val _openedChat = MutableStateFlow<ResolvedChatEntity?>(null)
    val openedChat: StateFlow<ResolvedChatEntity?> = _openedChat

    fun startChatWithUser(userId: String) {
        viewModelScope.launch {
            _openedChat.value = chatRepository.getResolvedChatByFriend(userId)
        }
    }

    fun resetCreationStatus() {
        _openedChat.value = null
    }

}