package com.cmpt362.cinebon.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatted(): String {
    val format = SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(this)
}