package com.pirra.chat.core.data.model

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val messageType: MessageType = MessageType.TEXT,
    val text: String,
    val timestamp: Long,
    val status: MessageStatus,
    val replyText: String? = null
)
