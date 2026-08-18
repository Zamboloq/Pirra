package com.pirra.chat.core.network.model

import androidx.annotation.Keep

@Keep
enum class MessageStatus(val value: String) {
    SENDING("SENDING"),
    SENT("SENT"),
    READ("READ"),
    FAILED("FAILED");

    companion object {
        fun fromString(status: String?): MessageStatus {
            return entries.find { it.value == status } ?: FAILED
        }
    }
}
