package com.pirra.chat.core.data.local.model

import com.pirra.chat.core.data.model.Chat
import com.pirra.chat.core.data.model.Message
import com.pirra.chat.core.data.model.MessageStatus
import com.pirra.chat.core.data.model.MessageType


fun MessageEntity.toDomain(): Message = Message(
    id = id,
    chatId = chatId,
    senderId = senderId,
    messageType = try { MessageType.valueOf(this.messageType) } catch (e: Exception) { MessageType.TEXT },
    text = text,
    timestamp = timestamp,
    status = MessageStatus.valueOf(status),
    replyText = replyText
)

fun Message.toEntity(): MessageEntity = MessageEntity(
    id = id,
    chatId = chatId,
    senderId = senderId,
    messageType = messageType.name,
    text = text,
    timestamp = timestamp,
    status = status.name,
    replyText = replyText
)

fun ChatEntity.toDomain(): Chat =
    Chat(
        id = id,
        recipientUid = recipientUid,
        recipientName = recipientName,
        lastMessage = lastMessage,
        timestamp = timestamp
    )

fun Chat.toEntity(): ChatEntity =
    ChatEntity(
        id = id,
        recipientUid = recipientUid,
        recipientName = recipientName,
        lastMessage = lastMessage,
        timestamp = timestamp
    )