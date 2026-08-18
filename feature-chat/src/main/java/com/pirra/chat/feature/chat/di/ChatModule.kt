package com.pirra.chat.feature.chat.di

import com.pirra.chat.feature.chat.ui.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chatModule = module {
    viewModel { ChatViewModel(get()) }
}