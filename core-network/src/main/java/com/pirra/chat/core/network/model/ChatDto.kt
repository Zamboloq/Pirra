package com.pirra.chat.core.network.model

import androidx.annotation.Keep

@Keep
data class ChatDto(
    val id: String = "",
    val recipientUid: String = "",
    val recipientName: String = "",
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "recipientUid" to recipientUid,
        "recipientName" to recipientName,
        "lastMessage" to lastMessage,
        "timestamp" to timestamp
    )
}
