package com.pirra.chat.feature.chat.ui

import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pirra.chat.core.data.ChatRepository
import com.pirra.chat.core.data.model.Message
import com.pirra.chat.core.data.model.MessageStatus
import com.pirra.chat.core.data.model.MessageType
import com.pirra.chat.feature.chat.audio.PirraAudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

private const val MAX_IMAGE_RESOLUTION_WIDTH = 1200
private const val IMAGE_COMPRESSION_QUALITY_PERCENTAGE = 75

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(ChatViewState())
    val viewState: StateFlow<ChatViewState> = _viewState.asStateFlow()

    init {
        onEvent(ChatViewEvent.CheckActiveSession)
    }

    fun onEvent(event: ChatViewEvent) {
        when (event) {
            is ChatViewEvent.CheckActiveSession -> verifyUserSession()
            is ChatViewEvent.TriggerSignOut -> terminateUserSession()
            is ChatViewEvent.SubmitRegistration -> createNewUser(
                event.email,
                event.password,
                event.name
            )

            is ChatViewEvent.SubmitLogin -> authenticateUser(event.email, event.password)
            is ChatViewEvent.LoadUserChats -> observeLiveUserChatsSummary()
            is ChatViewEvent.LoadMessages -> observeLiveChat(event.chatId)
            is ChatViewEvent.SendMessage -> dispatchNewMessage(
                event.chatId,
                event.senderId,
                event.text,
                event.recipientName,
                event.replyingToMessage
            )

            is ChatViewEvent.ObservePresence -> watchRecipientPresence(event.recipientUid)
            is ChatViewEvent.MarkMessagesAsRead -> executeMarkAsRead(event.chatId)
            is ChatViewEvent.UpdateTypingStatus -> handleTypingDispatch(
                event.chatId,
                event.isTyping
            )

            is ChatViewEvent.DeleteMessage -> executeMessageDeletion(event.chatId, event.messageId)
            is ChatViewEvent.SetReplyMessage -> _viewState.update { it.copy(replyingToMessage = event.message) }
            is ChatViewEvent.LoadContacts -> observeUserContactsList()
            is ChatViewEvent.SearchUserByEmail -> executeUserRemoteSearch(event.email)
            is ChatViewEvent.AddUserToContacts -> executeAddContactPipeline(event.contactUid)
            is ChatViewEvent.ClearSearchState -> resetDirectorySearchState()
            is ChatViewEvent.SendImageMessage -> dispatchImageMessage(
                event.chatId,
                event.senderId,
                event.imageBytes,
                event.recipientName
            )

            is ChatViewEvent.SendVoiceMessage -> dispatchVoiceMessage(
                event.chatId,
                event.senderId,
                event.audioBytes,
                event.recipientName
            )
//            is ChatViewEvent.PlayVoiceNote -> executeVoicePlayback(event.encryptedBase64Text)

        }
    }

    private fun dispatchImageMessage(
        chatId: String,
        senderId: String,
        imageBytes: ByteArray,
        recipientName: String
    ) {
        viewModelScope.launch {
            val originalBitmap =
                android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val scaledBitmap = if (originalBitmap.width > MAX_IMAGE_RESOLUTION_WIDTH) {
                val aspectRatio = originalBitmap.height.toFloat() / originalBitmap.width.toFloat()
                val targetHeight = (MAX_IMAGE_RESOLUTION_WIDTH * aspectRatio).toInt()
                android.graphics.Bitmap.createScaledBitmap(
                    originalBitmap,
                    MAX_IMAGE_RESOLUTION_WIDTH, targetHeight, true
                )
            } else {
                originalBitmap
            }

            val outputStream = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(
                android.graphics.Bitmap.CompressFormat.JPEG,
                IMAGE_COMPRESSION_QUALITY_PERCENTAGE, outputStream
            )
            val compressedBytes = outputStream.toByteArray()

            originalBitmap.recycle()
            if (scaledBitmap != originalBitmap) scaledBitmap.recycle()

            val base64ImageString = encodeToString(compressedBytes, NO_WRAP)

            Log.d(
                "PirraCompress",
                "📉 Compressed from ${imageBytes.size} bytes to ${compressedBytes.size} bytes!"
            )

            val freshMessage = Message(
                id = java.util.UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = senderId,
                text = base64ImageString,
                timestamp = System.currentTimeMillis(),
                status = com.pirra.chat.core.data.model.MessageStatus.SENDING,
                messageType = MessageType.IMAGE
            )
            try {
                repository.sendMessage(freshMessage, recipientName)
            } catch (e: Exception) {
                _viewState.update { it.copy(error = "Failed to dispatch media image: ${e.localizedMessage}") }
            }
        }
    }


    private var audioPlayer: PirraAudioPlayer? = null

//    private fun executeVoicePlayback(encryptedBase64Text: String) {
//        viewModelScope.launch {
//            try {
//                val encryptedBytes = android.util.Base64.decode(encryptedBase64Text, android.util.Base64.DEFAULT)
//                val clearPlaintextString = repository.getMessages("").toString() // Dummy reference mapping hook
//                val clearAudioBytes = android.util.Base64.decode(encryptedBase64Text, android.util.Base64.DEFAULT)
//
//                if (audioPlayer == null) {
//                    // TODO Initialize player context mapping dynamically if empty
//                    // We will hook this to app context initialization layers downstream
//                }
//            } catch (_: Exception) {}
//        }
//    }

    private fun dispatchVoiceMessage(
        chatId: String,
        senderId: String,
        audioBytes: ByteArray,
        recipientName: String
    ) {
        viewModelScope.launch {
            val rawAudioString =
                android.util.Base64.encodeToString(audioBytes, android.util.Base64.NO_WRAP)

            val freshMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = senderId,
                messageType = MessageType.VOICE,
                text = rawAudioString,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENDING
            )
            try {
                repository.sendMessage(freshMessage, recipientName)
            } catch (e: Exception) {
                _viewState.update { it.copy(error = "Failed to dispatch voice record: ${e.localizedMessage}") }
            }
        }
    }


    private fun handleTypingDispatch(chatId: String, isTyping: Boolean) {
        viewModelScope.launch {
            try {
                repository.sendTypingStatus(chatId, isTyping)
            } catch (_: Exception) {
            }
        }
    }

    private fun executeMessageDeletion(chatId: String, messageId: String) {
        viewModelScope.launch {
            try {
                repository.deleteMessage(chatId, messageId)
            } catch (_: Exception) {
            }
        }
    }

    private fun watchRecipientPresence(recipientUid: String) {
        viewModelScope.launch {
            repository.getUserPresence(recipientUid).collect { userPresence ->
                _viewState.update { it.copy(activeRecipientPresence = userPresence) }
            }
        }

        viewModelScope.launch {
            val chatId = _viewState.value.messages.firstOrNull()?.chatId ?: ""
            if (chatId.isNotBlank()) {
                repository.getTypingStatus(chatId, recipientUid).collect { typingState ->
                    _viewState.update { it.copy(isRecipientTyping = typingState) }
                }
            }
        }
    }

    private fun executeMarkAsRead(chatId: String) {
        viewModelScope.launch {
            try {
                repository.markAsRead(chatId)
            } catch (_: Exception) {
            }
        }
    }

    private fun observeLiveUserChatsSummary() {
        viewModelScope.launch {
            try {
                repository.getUserChatsList().collect { chatSummaryList ->
                    _viewState.update { it.copy(chats = chatSummaryList) }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(error = "Failed to load chats summary: ${e.localizedMessage}") }
            }
        }
    }

    private fun verifyUserSession() {
        val user = repository.getSessionUser()
        _viewState.update { it.copy(currentUser = user, isSessionChecked = true) }
        if (user != null) {
            viewModelScope.launch { repository.updatePresence(true) }
        }
    }

    private fun authenticateUser(email: String, javaPassword: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null) }
            try {
                val loggedUser = repository.login(email, javaPassword)
                _viewState.update { it.copy(isLoading = false, currentUser = loggedUser) }
                onEvent(ChatViewEvent.LoadContacts)
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun createNewUser(email: String, javaPassword: String, name: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null) }
            try {
                val registeredUser = repository.register(email, javaPassword, name)
                _viewState.update { it.copy(isLoading = false, currentUser = registeredUser) }
                onEvent(ChatViewEvent.LoadContacts)
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun terminateUserSession() {

        viewModelScope.launch {
            repository.updatePresence(false)
            repository.logout()
            _viewState.update { it.copy(currentUser = null, contacts = emptyList()) }
        }
    }

    private fun observeUserContactsList() {
        viewModelScope.launch {
            try {
                repository.getContacts().collect { contactList ->
                    _viewState.update { it.copy(contacts = contactList) }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(error = "Failed to load directory: ${e.localizedMessage}") }
            }
        }
    }

    private fun executeUserRemoteSearch(email: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, searchResult = null, searchError = null) }
            try {
                val user = repository.searchUser(email)
                if (user != null) {
                    _viewState.update { it.copy(isLoading = false, searchResult = user) }
                } else {
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            searchError = "No user found with this email account."
                        )
                    }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, searchError = e.localizedMessage) }
            }
        }
    }

    private fun executeAddContactPipeline(contactUid: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            try {
                repository.appendContact(contactUid)
                _viewState.update {
                    it.copy(
                        isLoading = false,
                        searchResult = null
                    )
                }
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        isLoading = false,
                        searchError = "Failed to add profile: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun resetDirectorySearchState() {
        _viewState.update { it.copy(searchResult = null, searchError = null) }
    }

    private fun observeLiveChat(chatId: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getMessages(chatId).collect { messageList ->
                    _viewState.update { it.copy(isLoading = false, messages = messageList) }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun dispatchNewMessage(
        chatId: String,
        senderId: String,
        text: String,
        recipientName: String,
        replyMessageId: String? = null
    ) {
        viewModelScope.launch {
            val parentReplyText = _viewState.value.messages.find { it.id == replyMessageId }?.text
            val freshMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = senderId,
                text = text,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENDING,
                replyText = parentReplyText
            )
            try {
                repository.sendMessage(freshMessage, recipientName)
            } catch (e: Exception) {
                _viewState.update { it.copy(error = "Failed to dispatch message: ${e.localizedMessage}") }
            }
        }
    }
}
