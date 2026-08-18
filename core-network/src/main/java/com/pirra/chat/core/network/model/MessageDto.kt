package com.pirra.chat.core.network.model

import androidx.annotation.Keep

@Keep
data class MessageDto(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = MessageStatus.SENDING.value,
    val replyText: String? = null,
    val messageType: String = "TEXT"
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "chatId" to chatId,
        "senderId" to senderId,
        "text" to text,
        "timestamp" to timestamp,
        "status" to status,
        "replyText" to replyText,
        "messageType" to messageType
    )
}
