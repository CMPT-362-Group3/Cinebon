package com.cmpt362.cinebon.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

// A helper composable function that sets the status bar color when a composable is loaded
@Composable
fun SetStatusBarColor(statusBarColor: Color) {
    val context = LocalContext.current

    if (!LocalInspectionMode.current) {
        SideEffect {
            (context as Activity).window.statusBarColor = statusBarColor.toArgb()
        }
    }
}