package com.pirra.chat

import android.app.Application
import com.pirra.chat.core.data.di.dataModule
import com.pirra.chat.core.network.di.networkModule
import com.pirra.chat.feature.chat.di.chatModule
import com.pirra.chat.core.crypto.di.cryptoModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PirraApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // راه‌اندازی لایه مدیریت وابستگی‌های پروژه Pirra به صورت یکپارچه
        startKoin {
            androidLogger()
            androidContext(this@PirraApplication)
            // اتصال تمام ماژول‌های جداگانه فرعی به هسته مرکزی
            modules(
                networkModule,
                dataModule,
                chatModule,
                cryptoModule
            )
        }
    }
}