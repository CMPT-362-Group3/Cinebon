package com.cmpt362.cinebon.data.entity

import com.google.firebase.firestore.Exclude

class Request {
    @set:Exclude @get:Exclude
    var requestId: String = ""
    var accepted: Boolean = false
    lateinit var receiver: String
    lateinit var sender: String
}