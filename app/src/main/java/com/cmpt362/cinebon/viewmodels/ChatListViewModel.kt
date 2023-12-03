package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import com.cmpt362.cinebon.data.repo.ChatRepository

class ChatListViewModel : ViewModel() {

    private val chatRepository = ChatRepository.getInstance()

    val resolvedChats = chatRepository.resolvedChats
}