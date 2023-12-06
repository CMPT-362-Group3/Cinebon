package com.cmpt362.cinebon.data.enums

// Enum class to represent the state of "friendship" between two users
// This is used to determine what to display on the friend profile screen page
enum class FriendRequestStatus {
    SENT, // Request sent, not accepted
    RECEIVED, // Request received, not accepted
    ACCEPTED, // Request accepted - you're already friends
    NONE // No active request exists
}