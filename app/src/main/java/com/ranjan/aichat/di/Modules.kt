package com.ranjan.aichat.di

import android.speech.SpeechRecognizer
import com.ranjan.aichat.model.Repo
import com.ranjan.aichat.model.botResponse.BotRepo
import com.ranjan.aichat.model.botResponse.BotRepoImpl
import com.ranjan.aichat.model.speech.SpeechRepo
import com.ranjan.aichat.model.speech.SpeechRepoImpl
import com.ranjan.aichat.ui.mainScreen.MainViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val module = module {
    singleOf(::Repo)
    single<SpeechRecognizer> { SpeechRecognizer.createSpeechRecognizer(get()) }
    single<SpeechRepo> { SpeechRepoImpl(get()) }
    single<BotRepo> { BotRepoImpl() }
}


val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}