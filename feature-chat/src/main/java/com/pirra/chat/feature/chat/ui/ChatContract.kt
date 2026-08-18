package com.pirra.chat.feature.chat.ui

import com.pirra.chat.core.data.model.Chat
import com.pirra.chat.core.data.model.Message
import com.pirra.chat.core.data.model.User

data class ChatViewState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val currentUser: User? = null,
    val isSessionChecked: Boolean = false,
    val contacts: List<User> = emptyList(),
    val searchResult: User? = null,
    val chats: List<Chat> = emptyList(),
    val activeRecipientPresence: User? = null,
    val isRecipientTyping: Boolean = false,
    val replyingToMessage: Message? = null,
    val searchError: String? = null,
    val error: String? = null
)

sealed interface ChatViewEvent {
    object CheckActiveSession : ChatViewEvent
    object TriggerSignOut : ChatViewEvent

    data class SubmitRegistration(val email: String, val password: String, val name: String) :
        ChatViewEvent

    data class SubmitLogin(val email: String, val password: String) : ChatViewEvent

    data class LoadMessages(val chatId: String) : ChatViewEvent
    data class SendMessage(
        val chatId: String,
        val senderId: String,
        val text: String,
        val recipientName: String,
        val replyingToMessage: String?
    ) : ChatViewEvent

    data class ObservePresence(val recipientUid: String) : ChatViewEvent
    data class MarkMessagesAsRead(val chatId: String) : ChatViewEvent

    data class UpdateTypingStatus(val chatId: String, val isTyping: Boolean) : ChatViewEvent
    data class DeleteMessage(val chatId: String, val messageId: String) : ChatViewEvent
    data class SetReplyMessage(val message: Message?) : ChatViewEvent

    object LoadContacts : ChatViewEvent
    object LoadUserChats : ChatViewEvent
    data class SearchUserByEmail(val email: String) : ChatViewEvent
    data class AddUserToContacts(val contactUid: String) : ChatViewEvent
    object ClearSearchState : ChatViewEvent

    data class SendImageMessage(
        val chatId: String,
        val senderId: String,
        val imageBytes: ByteArray,
        val recipientName: String
    ) : ChatViewEvent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SendImageMessage

            if (chatId != other.chatId) return false
            if (senderId != other.senderId) return false
            if (!imageBytes.contentEquals(other.imageBytes)) return false
            if (recipientName != other.recipientName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = chatId.hashCode()
            result = 31 * result + senderId.hashCode()
            result = 31 * result + imageBytes.contentHashCode()
            result = 31 * result + recipientName.hashCode()
            return result
        }
    }

    data class SendVoiceMessage(
        val chatId: String,
        val senderId: String,
        val audioBytes: ByteArray,
        val recipientName: String
    ) : ChatViewEvent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SendVoiceMessage

            if (chatId != other.chatId) return false
            if (senderId != other.senderId) return false
            if (!audioBytes.contentEquals(other.audioBytes)) return false
            if (recipientName != other.recipientName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = chatId.hashCode()
            result = 31 * result + senderId.hashCode()
            result = 31 * result + audioBytes.contentHashCode()
            result = 31 * result + recipientName.hashCode()
            return result
        }
    }
//    data class PlayVoiceNote(val encryptedBase64Text: String) : ChatViewEvent

}
