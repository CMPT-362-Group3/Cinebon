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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ChatService : Service() {

    companion object {
        const val CHANNEL_ID = "Cinebon_chat"
        const val SERVICE_NOTIFICATION_ID = -1
    }

    private val userRepository = UserRepository.getInstance()
    private val chatRepository = ChatRepository.getInstance()
    private val listRepository = ListRepository.getInstance()
    private val serviceScope = CoroutineScope(Default)

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()

        // Create a notification and start the service as a foreground service
        showForegroundNotification()

        // Start the chat listener worker coroutine to listen for chat updates
        startChatWorker()

        // Start teh list refresh worker coroutine to listen for list updates
        startListWorker()

        // Trigger a user data update manually to ensure that the user data is up to date
        serviceScope.launch { userRepository.updateCurrentUserData() }
    }

    private fun startChatWorker() {
        serviceScope.launch {
            userRepository.attachUserRefListener(userRefListener)
        }

        serviceScope.launch {
            chatRepository.startChatRefreshWorker()
        }

        serviceScope.launch {
            Log.d("ChatService", "Starting chat worker")
            chatRepository.attachChatRefsWorker()
        }
    }

    private fun startListWorker() {
        serviceScope.launch {
            listRepository.startListRefreshWorker()
        }
    }

    // A user doc listener that gets notified upon any changes made to user
    // We only use it to receive latest user chats state
    private val userRefListener = EventListener<DocumentSnapshot> { snapshot, e ->
        if (e != null) {
            Log.w("ChatService", "Listen failed", e)
            return@EventListener
        }

        Log.w("ChatService", "User ref snapshot updated: $snapshot")
        serviceScope.launch {
            userRepository.updateCurrentUserData()
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