package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

class MessageEntity {

    companion object {
        const val TIMESTAMP_FIELD = "timestamp"
    }

    var sender: DocumentReference? = null

    var timestamp: Timestamp = Timestamp(0, 0)

    lateinit var text: String
}

data class ResolvedMessageEntity(
    val sender: User,

    @ServerTimestamp
    val timestamp: Date,

    val isSelf: Boolean,

    val text: String
)

data class PackagedMessageEntity(
    val sender: DocumentReference,

    @ServerTimestamp
    val timestamp: Date,

    val text: String
)