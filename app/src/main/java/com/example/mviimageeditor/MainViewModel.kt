package com.example.mviimageeditor

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.mviimageeditor.nav.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : BaseViewModel()