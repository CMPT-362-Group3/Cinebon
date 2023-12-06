package com.cmpt362.cinebon.data.entity

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
    val receiver: UserEntity,
    val sender: UserEntity
)