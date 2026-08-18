package com.pirra.chat.core.network

import com.pirra.chat.core.network.model.ChatDto
import com.pirra.chat.core.network.model.MessageDto
import com.pirra.chat.core.network.model.UserDto
import kotlinx.coroutines.flow.Flow

interface FirebaseChatDataSource {
    suspend fun sendMessage(message: MessageDto)
    fun observeMessages(chatId: String): Flow<List<MessageDto>>
    suspend fun registerUser(email: String, password: String, displayName: String): UserDto
    suspend fun loginUser(email: String, password: String): UserDto
    fun getCurrentUser(): UserDto?
    suspend fun logoutUser()
    suspend fun searchUserByEmail(email: String): UserDto?
    suspend fun addContact(contactUid: String)
    fun observeContacts(): Flow<List<UserDto>>
    fun observeUserChats(): Flow<List<ChatDto>>
    suspend fun createOrUpdateChatRoom(
        chatId: String,
        recipientUid: String,
        recipientName: String,
        lastText: String
    )
    suspend fun markMessagesAsRead(chatId: String, currentUserId: String)
    suspend fun updateUserPresence(isOnline: Boolean)
    fun observeUserPresence(userUid: String): Flow<UserDto?>
    fun observeTypingStatus(chatId: String, recipientUid: String): Flow<Boolean>
    suspend fun setTypingStatus(chatId: String, isTyping: Boolean)
    suspend fun remoteDeleteMessage(chatId: String, messageId: String)
}
