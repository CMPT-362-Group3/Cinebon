package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.ResolvedChatEntity
import com.cmpt362.cinebon.data.repo.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IndividualChatViewModel(id: String) : ViewModel() {

    private val chatRepository = ChatRepository.getInstance()

    private val _currentChat = MutableStateFlow(
        chatRepository.getResolvedChatById(id)
    )
    val currentChat: StateFlow<ResolvedChatEntity?>
        get() = _currentChat

    init {
        // When the VM initializes, track the chat with the given id
        // And keep it plugged to the UI whenever resolved chats update
        viewModelScope.launch {
            chatRepository.resolvedChats.collectLatest {
                _currentChat.value = chatRepository.getResolvedChatById(id)
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(_currentChat.value!!, text)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IndividualChatViewModel(id) as T
        }
    }
}