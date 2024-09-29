package com.ranjan.aichat

import android.app.Application
import com.ranjan.aichat.di.module
import com.ranjan.aichat.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(module, viewModelModule)
        }
    }
}