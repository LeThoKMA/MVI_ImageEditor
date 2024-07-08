package com.example.mviimageeditor.nav

interface Navigator {
    fun navigate(screen: Screen)
    fun navigateBack()
    fun navigateToChild(screen: Screen)
}