package com.pirra.chat.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val messageType: String = "TEXT",
    val text: String,
    val timestamp: Long,
    val status: String,
    val replyText: String? = null
)
