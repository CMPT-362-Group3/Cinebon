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
    private val serviceScope = CoroutineScope(Default)

    override fun onBind(p0: Intent?): IBinder {
        Log.d("ChatService", "Chat service bound")
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("ChatService", "Chat service created")

        // Create a notification and start the service as a foreground service
        showForegroundNotification()

        // Start the chat listener worker coroutine to listen for chat updates
        startChatWorker()
    }

    private fun startChatWorker() {
        serviceScope.launch {
            userRepository.userChats.collect {
                Log.d("ChatService", "User chats updated: $it")
                for (chat in it) {
                    println("ChatService: $chat")
                }
            }
        }

        serviceScope.launch {
            userRepository.attachUserRefListener(userRefListener)
        }
    }

    // A user doc listener that gets notified upon any changes made to user
    // We only use it to receive latest user chats state
    private val userRefListener = EventListener<DocumentSnapshot> { snapshot, e ->
        if (e != null) {
            Log.w("ChatService", "Listen failed", e)
            return@EventListener
        }

        Log.w("ChatService", "User ref snapshot updated")
        userRepository.getUserChats()
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