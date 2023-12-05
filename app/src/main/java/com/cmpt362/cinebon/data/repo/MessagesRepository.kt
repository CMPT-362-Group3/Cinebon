package com.cmpt362.cinebon.data.repo

import com.cmpt362.cinebon.data.entity.ChatEntity
import com.cmpt362.cinebon.data.entity.MessageEntity
import com.cmpt362.cinebon.data.entity.MessageEntity.Companion.TIMESTAMP_FIELD
import com.cmpt362.cinebon.data.entity.ResolvedMessageEntity
import com.cmpt362.cinebon.data.entity.messagePath
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class MessagesRepository private constructor() {

    companion object {

        private val instance = MessagesRepository()

        fun getInstance(): MessagesRepository {
            return instance
        }
    }

    private val database = Firebase.firestore
    private val userRepo = UserRepository.getInstance()

    // This function takes a chat entity and returns a list of resolved messages
    // based on standard query parameters
    suspend fun getResolvedMessages(chat: ChatEntity): List<ResolvedMessageEntity> {
        val messagesRef = database.collection(chat.messagePath())
        val messages = mutableListOf<MessageEntity>()

        messagesRef.orderBy(TIMESTAMP_FIELD).limit(200)
            .get().await().forEach { messageDoc ->
                messageDoc.toObject<MessageEntity>().let {
                    messages.add(it)
                }
            }

        val resolvedMessages = mutableListOf<ResolvedMessageEntity>()

        messages.forEach {
            // Bail out if user info are null for some reason - possibly due to deletion de-sync
            if (it.sender == null) {
                return@forEach
            }

            val sender = userRepo.getUserData(it.sender!!.id) ?: return@forEach

            // Bail out if user info are null for some reason - possibly due to deletion de-sync

            resolvedMessages.add(
                ResolvedMessageEntity(
                    sender,
                    it.timestamp.toDate(),
                    sender.userId == userRepo.userInfo.value?.userId,
                    it.text
                )
            )
        }

        return resolvedMessages
    }
}