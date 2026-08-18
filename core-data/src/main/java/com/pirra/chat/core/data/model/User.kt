package com.pirra.chat.core.data.model

data class User(
    val uid: String,
    val displayName: String,
    val email: String,
    val isOnline: Boolean,
    val lastSeen: Long
)
