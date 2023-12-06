package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude

class ChatEntity {

    companion object {
        const val CHAT_COLLECTION = "chats"
        const val CHAT_MESSAGES = "messages"
    }

    var users = mutableListOf<DocumentReference>()

    @set:Exclude @get:Exclude
    var chatId: String = ""
}

data class ResolvedChatEntity(
    val others: List<User>,
    val chatId: String,
    val messages: List<ResolvedMessageEntity>
)

fun ChatEntity.messagePath() = "${ChatEntity.CHAT_COLLECTION}/${this.chatId}/${ChatEntity.CHAT_MESSAGES}"
fun ResolvedChatEntity.messagePath() = "${ChatEntity.CHAT_COLLECTION}/${this.chatId}/${ChatEntity.CHAT_MESSAGES}"