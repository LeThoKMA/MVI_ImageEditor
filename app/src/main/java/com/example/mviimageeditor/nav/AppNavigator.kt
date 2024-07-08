package com.example.mviimageeditor.nav

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.ui.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel

val LocalAppNavigator = staticCompositionLocalOf<Navigator> {
    error("No navigator provided")
}