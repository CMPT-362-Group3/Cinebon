package com.cmpt362.cinebon.data.repo

import android.util.Log
import com.cmpt362.cinebon.data.entity.ChatEntity
import com.cmpt362.cinebon.data.entity.PackagedMessageEntity
import com.cmpt362.cinebon.data.entity.ResolvedChatEntity
import com.cmpt362.cinebon.data.entity.messagePath
import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class ChatRepository private constructor() {

    companion object {

        private val instance = ChatRepository()

        fun getInstance(): ChatRepository {
            return instance
        }
    }

    private val database = Firebase.firestore
    private val userRepo = UserRepository.getInstance()
    private val messagesRepo = MessagesRepository.getInstance()

    private val _resolvedChats = MutableStateFlow<List<ResolvedChatEntity>>(emptyList())
    val resolvedChats: StateFlow<List<ResolvedChatEntity>>
        get() = _resolvedChats

    suspend fun attachChatRefsWorker() {
        // Start the chat refs worker
        userChats.collectLatest { chats ->
            resolveChats(chats)
        }
    }

    private suspend fun resolveChats(chats: List<ChatEntity>) {
        Log.d("ChatRepository", "User chats updated with size ${chats.size}")

        // Create a new resolved chats list
        val resolvedChats = mutableListOf<ResolvedChatEntity>()

        // For each chat, resolve the user references
        chats.forEach { chat ->
            val resolvedUsers = mutableListOf<User>()
            Log.d("ChatRepository", "Resolving chat ${chat.chatId}")

            // For each user in the chat, resolve the user data
            for (userRef in chat.users) {
                if (userRef.id == userRepo.userInfo.value?.userId) continue

                val user = userRepo.getUserData(userRef.id)
                if (user != null) {
                    resolvedUsers.add(user)
                }
            }

            // Fetch and resolve messages for this chat
            val messagesList = messagesRepo.getResolvedMessages(chat)

            // Add the resolved chat to the resolved chats list
            resolvedChats.add(
                ResolvedChatEntity(
                    others = resolvedUsers,
                    chatId = chat.chatId,
                    messages = messagesList
                )
            )
            Log.d("ChatRepository", "Resolved chat ${chat.chatId}")
        }

        // Update the resolved chats list
        _resolvedChats.value = resolvedChats
        Log.d("ChatRepository", "Resolved chats updated")
    }

    suspend fun forceResolveChats() {
        resolveChats(userChats.value)
    }

    private val _userChats = MutableStateFlow<List<ChatEntity>>(emptyList())
    val userChats: StateFlow<List<ChatEntity>>
        get() = _userChats

    // Changes in user info may be due to added or removed chats.
    // This worker will listen to whenever there's a change in user info
    // And will update the user chats list accordingly.
    suspend fun startChatRefreshWorker() {
        // Listen to updates in user info and repopulate user chats when it changes
        userRepo.userInfo.collectLatest {
            if (it == null) return@collectLatest

            Log.d("ChatRepository", "User info updated, updating chats")
            updateUserChats(it)
        }
    }

    // Sub-function to get chats from user object
    private suspend fun updateUserChats(user: User?) {
        if (user == null) _userChats.value = emptyList()

        Log.d("UserRepository", "Getting user chats from user object")
        val chatRefs = user!!.chats
        val chats = mutableListOf<ChatEntity>()

        flow {
            for (chatRef in chatRefs) {
                chatRef.get().await().apply {
                    toObject<ChatEntity>()?.let {
                        it.chatId = this.id
                        emit(it)
                    }
                }
            }
        }.onCompletion {
            Log.d("UserRepository", "User chats updated with size ${chats.size}")
            _userChats.value = chats
        }.collect {
            chats.add(it)
        }
    }

    fun attachMessagesRefListener(chat: ChatEntity, listener: EventListener<QuerySnapshot>) {
        val messagesRef = database.collection(chat.messagePath())
        messagesRef.addSnapshotListener(listener)
    }

    suspend fun sendMessage(chat: ResolvedChatEntity, text: String) {
        withContext(IO) {
            val message = PackagedMessageEntity(
                userRepo.getUserRef(userRepo.userInfo.value!!.userId),
                Date.from(Calendar.getInstance().toInstant()),
                text
            )

            Log.d("ChatRepository", "Sending message: $message")

            database.collection(chat.messagePath()).add(message)
        }
    }
}