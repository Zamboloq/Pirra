package com.pirra.chat.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pirra.chat.core.data.local.model.ChatEntity
import com.pirra.chat.core.data.local.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM cached_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun observeMessagesByChatId(chatId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleMessage(message: MessageEntity)

    @Query("DELETE FROM cached_messages WHERE chatId = :chatId")
    suspend fun clearChatCache(chatId: String)

    @Query("SELECT * FROM cached_chats ORDER BY timestamp DESC")
    fun observeAllCachedChats(): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatSummaries(chats: List<ChatEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleChatSummary(chat: ChatEntity)

    @Query("DELETE FROM cached_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String)

}
