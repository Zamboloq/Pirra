package com.pirra.chat.core.data.model

import com.pirra.chat.core.network.model.ChatDto

fun ChatDto.toDomain(): Chat = Chat(
    id = id,
    recipientUid = recipientUid,
    recipientName = recipientName,
    lastMessage = lastMessage,
    timestamp = timestamp
)
