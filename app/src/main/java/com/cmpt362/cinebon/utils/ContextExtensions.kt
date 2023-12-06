package com.cmpt362.cinebon.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

// A simple helper function to check if the app has notification permissions
fun Context.hasNotificationPerm(): Boolean {
    return ContextCompat.checkSelfPermission(
        this.applicationContext,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}