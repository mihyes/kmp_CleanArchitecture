package com.example.demokmpapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import di.AndroidDIContainer

@HiltAndroidApp
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