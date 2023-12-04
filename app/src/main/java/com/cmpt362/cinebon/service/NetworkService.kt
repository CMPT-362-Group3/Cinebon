package com.cmpt362.cinebon.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.cmpt362.cinebon.MainActivity
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.repo.ChatRepository
import com.cmpt362.cinebon.data.repo.ListRepository
import com.cmpt362.cinebon.data.repo.UserRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NetworkService : Service() {

    companion object {
        const val CHANNEL_ID = "Cinebon_chat"
        const val SERVICE_NOTIFICATION_ID = -1
    }

    private val userRepository = UserRepository.getInstance()
    private val chatRepository = ChatRepository.getInstance()
    private val listRepository = ListRepository.getInstance()

    private val serviceScope = CoroutineScope(IO)

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()

        // Create a notification and start the service as a foreground service
        showForegroundNotification()

        // Start the current user worker coroutine to listen for chat updates
        startUserWorker()

        // Start the chat listener worker coroutine to listen for chat updates
        startChatWorkers()

        // Start the list refresh worker coroutine to listen for list updates
        startListWorker()

        // Trigger a user data update manually to ensure that the user data is up to date
        serviceScope.launch { userRepository.updateCurrentUserData() }
    }

    private fun startUserWorker() {
        serviceScope.launch {
            userRepository.attachUserRefListener(userRefListener)
        }
    }

    private fun startChatWorkers() {
        serviceScope.launch {
            chatRepository.startChatRefreshWorker()
        }

        serviceScope.launch {
            startMessagesRefreshWorker()
        }

        serviceScope.launch {
            chatRepository.attachChatResolverWorker()
        }
    }

    private fun startListWorker() {
        serviceScope.launch {
            listRepository.startListRefreshWorker()
        }

        serviceScope.launch {
            startListUpdateListenerWorker()
        }

        serviceScope.launch {
            listRepository.attachListResolverWorker()
        }
    }

    // A user doc listener that gets notified upon any changes made to user
    // We only use it to receive latest user chats state
    private val userRefListener = EventListener<DocumentSnapshot> { snapshot, e ->
        if (e != null) {
            Log.w("ChatService", "User ref listen failed", e)
            return@EventListener
        }

        Log.w("ChatService", "User ref snapshot updated: $snapshot")
        serviceScope.launch {
            userRepository.updateCurrentUserData()
        }
    }

    // A messages collection listener that gets notified upon any changes made to the messages
    // in a chat. We use it to update the resolved chats list with message changes.
    private val messagesRefListener = EventListener<QuerySnapshot> { snapshot, e ->
        if (e != null) {
            Log.w("ChatService", "Messages ref listen failed", e)
            return@EventListener
        }

        Log.w("ChatService", "Messages ref snapshot updated: $snapshot")
        serviceScope.launch {
            chatRepository.forceResolveChats()
        }
    }

    // This worker reacts to the new list of chats user is part of
    // and then listens to the messages collection in those chat documents.
    private suspend fun startMessagesRefreshWorker() {
        // Observe user authenticated chats
        chatRepository.userChats.collectLatest {
            // Cancel any previously registered message collection observers (they may be invalid now)
            chatRepository.invalidateMessageRefsListeners()

            // For each chat, start a message listener coroutine
            // Whenever a message document updates, update the resolved chats list.
            for (chat in it) {
                Log.d("ChatService", "Attaching messages ref listener for chat ${chat.chatId}")
                chatRepository.attachMessagesRefListener(chat, messagesRefListener)
            }
        }
    }

    // A list doc listener that gets notified upon any changes made to a given list record
    // We only use it to receive latest list state - because the user subscribes to the list.
    private val listRefListener = EventListener<DocumentSnapshot> { snapshot, e ->
        if (e != null) {
            Log.w("ChatService", "User ref listen failed", e)
            return@EventListener
        }

        Log.w("ChatService", "User ref snapshot updated: $snapshot")
        serviceScope.launch {
            listRepository.forceResolveLists()
        }
    }

    // This worker reacts to the new list of chats user is subscribed to
    // and then listens to the messages collection in those chat documents.
    private suspend fun startListUpdateListenerWorker() {
        // Observe user subscribed lists
        listRepository.userLists.collectLatest {
            // Cancel any previously registered list observers (they may be invalid now)
            listRepository.invalidateListRefsListeners()

            // For each chat, start a message listener coroutine
            // Whenever a message document updates, update the resolved chats list.
            for (list in it) {
                Log.d("ChatService", "Attaching messages ref listener for chat ${list.listId}")
                listRepository.attachListRefListener(list, listRefListener)
            }
        }
    }

    private fun showForegroundNotification() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
        notificationBuilder.setSmallIcon(R.drawable.cinebon)
        notificationBuilder.setContentTitle("Cinebon chat service")
        notificationBuilder.setContentText("Tap here to open the app")
        notificationBuilder.setContentIntent(pendingIntent)
        val notification = notificationBuilder.build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
        ServiceCompat.startForeground(this, SERVICE_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    override fun onDestroy() {
        Log.d("ChatService", "Chat service destroyed")

        // Cancel all listeners in the coroutine scope
        serviceScope.cancel()

        // Cancel the notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(SERVICE_NOTIFICATION_ID)

        super.onDestroy()
    }
}