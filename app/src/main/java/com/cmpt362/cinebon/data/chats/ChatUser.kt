package com.cmpt362.cinebon.data.chats

data class ChatUser(
    val id: String,
    val name: String, //get username
    val lastMessage: String,
    val lastDate:String)