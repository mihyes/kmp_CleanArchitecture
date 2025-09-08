package com.example.demokmpapp

import android.app.Application
import di.AndroidDIContainer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level



class AndroidApplication: Application() {
    val diContainer: AndroidDIContainer by lazy {
        AndroidDIContainer.getInstance(this)
    }
    override fun onCreate() {
        super.onCreate()
        println("🚀 Android DIContainer initialized")

//        startKoin {
//            androidLogger(Level.DEBUG)
//
//            androidContext(this@App)

//            modules(androidMoule)
//        }
    }
}