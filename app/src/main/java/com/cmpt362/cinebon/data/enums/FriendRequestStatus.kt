package com.cmpt362.cinebon.data.enums

enum class FriendRequestStatus {
    SENT, // Request sent, not accepted
    RECEIVED, // Request received, not accepted
    ACCEPTED, // Request accepted - you're already friends
    NONE // No active request exists
}