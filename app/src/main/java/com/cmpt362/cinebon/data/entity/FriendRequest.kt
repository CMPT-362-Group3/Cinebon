package com.cmpt362.cinebon.data.entity

import com.cmpt362.cinebon.data.objects.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude

class FriendRequest {

    @set:Exclude @get:Exclude
    var requestId: String = ""

    lateinit var receiver: DocumentReference
    lateinit var sender: DocumentReference
}

data class ResolvedFriendRequest(
    val requestId: String,
    val receiver: User,
    val sender: User
)