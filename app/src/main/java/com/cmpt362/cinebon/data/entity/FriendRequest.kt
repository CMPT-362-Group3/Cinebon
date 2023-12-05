package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude

class FriendRequest {

    companion object {
        const val REQUEST_SENDER = "sender"
        const val REQUEST_RECEIVER = "receiver"
    }

    @set:Exclude @get:Exclude
    var requestId: String = ""
    var accepted: Boolean = false
    lateinit var receiver: DocumentReference
    lateinit var sender: DocumentReference
}

data class ResolvedFriendRequest(
    val requestId: String,
    val accepted: Boolean,
    val receiver: User,
    val sender: User
)