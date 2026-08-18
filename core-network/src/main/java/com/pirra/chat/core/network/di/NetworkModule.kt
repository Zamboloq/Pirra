package com.pirra.chat.core.network.di

import com.google.firebase.firestore.FirebaseFirestore
import com.pirra.chat.core.network.FirebaseChatDataSource
import com.pirra.chat.core.network.FirebaseChatDataSourceImpl
import org.koin.dsl.module

val networkModule = module {
    single { FirebaseFirestore.getInstance() }

    single<FirebaseChatDataSource> { FirebaseChatDataSourceImpl(get()) }
}