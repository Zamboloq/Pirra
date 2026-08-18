package com.pirra.chat.core.data.model

import com.pirra.chat.core.network.model.UserDto

fun UserDto.toDomain(): User = User(
    uid = uid,
    displayName = displayName,
    email = email,
    isOnline = isOnline,
    lastSeen = lastSeen
)

fun User.toDto(): UserDto = UserDto(
    uid = uid,
    displayName = displayName,
    email = email,
    isOnline = isOnline,
    lastSeen = lastSeen
)
