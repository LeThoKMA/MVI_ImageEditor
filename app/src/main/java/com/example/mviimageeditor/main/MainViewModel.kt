package com.example.mviimageeditor.main

import androidx.lifecycle.SavedStateHandle
import com.example.mviimageeditor.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(),
    MainContract {
    private val _state = MutableStateFlow(MainContract.State())
    private val _effect = MutableSharedFlow<MainContract.Effect>()
    override val state: StateFlow<MainContract.State>
        get() = _state.asStateFlow()
    override val effect: SharedFlow<MainContract.Effect>
        get() = _effect.asSharedFlow()

    override fun event(event: MainContract.Event) {
    }
}
