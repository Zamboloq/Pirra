package com.pirra.chat.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pirra.chat.core.data.local.model.ChatEntity
import com.pirra.chat.core.data.local.model.MessageEntity

@Database(entities = [MessageEntity::class, ChatEntity::class], version = 4, exportSchema = false)
abstract class PirraDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
