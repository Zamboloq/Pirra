package com.pirra.chat.core.data.di

import androidx.room.Room
import com.pirra.chat.core.data.ChatRepository
import com.pirra.chat.core.data.ChatRepositoryImpl
import com.pirra.chat.core.data.local.PirraDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            PirraDatabase::class.java,
            "pirra_secure_local_store.db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<PirraDatabase>().messageDao() }
}