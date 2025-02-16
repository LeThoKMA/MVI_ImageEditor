package com.example.mviimageeditor.module

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    fun io(): kotlinx.coroutines.CoroutineDispatcher

    fun main(): kotlinx.coroutines.CoroutineDispatcher

    fun default(): kotlinx.coroutines.CoroutineDispatcher
}

class DispatcherProviderImpl : DispatcherProvider {
    override fun io(): CoroutineDispatcher = Dispatchers.IO

    override fun main() = Dispatchers.Main

    override fun default(): CoroutineDispatcher = Dispatchers.Default
}
