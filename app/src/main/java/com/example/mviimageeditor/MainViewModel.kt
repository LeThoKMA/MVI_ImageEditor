package com.example.mviimageeditor

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.mviimageeditor.nav.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _navController = MutableStateFlow<Screen>(Screen.Home)
    val navController = _navController.asStateFlow()
    fun navigateTo(screen: Screen) {
        _navController.value = screen
    }
}