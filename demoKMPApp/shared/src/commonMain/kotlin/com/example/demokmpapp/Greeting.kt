package com.example.demokmpapp

@kotlin.js.JsExport
class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}