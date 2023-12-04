package com.cmpt362.cinebon.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.cmpt362.cinebon.service.NetworkService

class DashBoardViewModel(private val app: Application) : AndroidViewModel(app) {

    private var isRunning = false

    // Ensure that the chat service is running
    // This prevents more start calls on recompositions in the same app instance
    // But does not prevent multiple start calls across multiple app launches
    // However, since the service is foreground and unbound, there's only 1 instance of it
    fun ensureRunningChatService() {
        if (isRunning) return

        Log.d("DashBoardViewModel", "Starting chat service")
        app.startForegroundService(Intent(app.applicationContext, NetworkService::class.java))
        isRunning = true
    }
}