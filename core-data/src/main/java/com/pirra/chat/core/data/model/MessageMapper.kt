package com.pirra.chat.core.data.model

import com.pirra.chat.core.network.model.MessageDto
import com.pirra.chat.core.network.model.MessageStatus as NetworkStatus


fun MessageDto.toDomain(): Message {
    return Message(
        id = this.id,
        chatId = this.chatId,
        senderId = this.senderId,
        messageType = try {
            MessageType.valueOf(this.messageType)
        } catch (e: Exception) {
            MessageType.TEXT
        },
        text = this.text,
        timestamp = this.timestamp,
        status = when (status) {
            NetworkStatus.SENDING.value -> MessageStatus.SENDING
            NetworkStatus.SENT.value -> MessageStatus.SENT
            NetworkStatus.READ.value -> MessageStatus.READ
            else -> MessageStatus.FAILED
        },
        replyText = replyText
    )
}

fun Message.toDto(): MessageDto {
    return MessageDto(
        id = this.id,
        chatId = this.chatId,
        senderId = this.senderId,
        messageType = messageType.name,
        text = this.text,
        timestamp = this.timestamp,
        status = when (status) {
            MessageStatus.SENDING -> NetworkStatus.SENDING.value
            MessageStatus.SENT -> NetworkStatus.SENT.value
            MessageStatus.READ -> NetworkStatus.READ.value
            MessageStatus.FAILED -> NetworkStatus.FAILED.value
        },
        replyText = replyText
    )
}
