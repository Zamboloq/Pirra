package com.pirra.chat.core.data.model

data class Chat(
    val id: String,
    val recipientUid: String,
    val recipientName: String,
    val lastMessage: String,
    val timestamp: Long
)
