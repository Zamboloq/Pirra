package com.pirra.chat.core.network.model

import androidx.annotation.Keep

@Keep
data class UserDto(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "displayName" to displayName,
        "email" to email,
        "isOnline" to isOnline,
        "lastSeen" to lastSeen
    )
}
