package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.DocumentReference

class ChatEntity {

    companion object {
        const val CHAT_COLLECTION = "chats"
    }

    var users = mutableListOf<DocumentReference>()
    lateinit var chatId: String
}

data class ResolvedChatEntity(
    val others: List<User>,
    val chatId: String
)