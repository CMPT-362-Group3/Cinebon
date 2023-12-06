package com.cmpt362.cinebon.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// A helper function to format a date to a proper time string
// This is used in the chat screen to display the time of a message
fun Date.formatted(): String {
    val format = SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(this)
}