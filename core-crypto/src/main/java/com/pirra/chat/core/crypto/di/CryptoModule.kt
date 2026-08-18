package com.pirra.chat.core.crypto.di

import com.pirra.chat.core.crypto.PirraCryptoEngine
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val cryptoModule = module {
    single { PirraCryptoEngine(androidContext()) }
}
