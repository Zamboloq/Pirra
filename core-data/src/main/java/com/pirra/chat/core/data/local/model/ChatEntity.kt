package com.pirra.chat.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val recipientUid: String,
    val recipientName: String,
    val lastMessage: String,
    val timestamp: Long
)
