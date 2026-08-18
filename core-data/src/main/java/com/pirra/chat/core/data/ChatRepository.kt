package com.pirra.chat.core.data

import com.pirra.chat.core.data.model.Chat
import com.pirra.chat.core.data.model.Message
import com.pirra.chat.core.data.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(message: Message, recipientName: String)
    fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun register(email: String, password: String, displayName: String): User
    suspend fun login(email: String, password: String): User
    fun getSessionUser(): User?
    suspend fun logout()
    suspend fun searchUser(email: String): User?
    suspend fun appendContact(contactUid: String)
    fun getContacts(): Flow<List<User>>
    fun getUserChatsList(): Flow<List<Chat>>
    suspend fun updatePresence(isOnline: Boolean)
    fun getUserPresence(userUid: String): Flow<User?>
    suspend fun markAsRead(chatId: String)
    fun getTypingStatus(chatId: String, recipientUid: String): Flow<Boolean>
    suspend fun sendTypingStatus(chatId: String, isTyping: Boolean)
    suspend fun deleteMessage(chatId: String, messageId: String)
}
