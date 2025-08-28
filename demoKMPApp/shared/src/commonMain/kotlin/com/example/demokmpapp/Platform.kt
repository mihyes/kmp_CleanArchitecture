package com.example.demokmpapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform