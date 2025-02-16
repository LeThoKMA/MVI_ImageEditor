package com.example.mviimageeditor.ui.create

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.mviimageeditor.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CaptureImageViewmodel :
    BaseViewModel(),
    CaptureImageContract {
    private val _state = MutableStateFlow(CaptureImageContract.State())
    private val _effect = MutableSharedFlow<CaptureImageContract.Effect>()
    override val effect: SharedFlow<CaptureImageContract.Effect>
        get() = _effect.asSharedFlow()
    override val state: StateFlow<CaptureImageContract.State>
        get() = _state.asStateFlow()

    override fun event(event: CaptureImageContract.Event) {
        viewModelScope.launch {
            when (event) {
                CaptureImageContract.Event.OnCapture -> {
                    _effect.emit(CaptureImageContract.Effect.CaptureImage)
                }

                CaptureImageContract.Event.OnFlash -> {
                    _effect.emit(CaptureImageContract.Effect.FlashImage)
                }

                CaptureImageContract.Event.OnSwitchCamera -> {
                    _effect.emit(CaptureImageContract.Effect.SwitchCamera)
                }

                is CaptureImageContract.Event.OnCaptureSuccess -> onCaptureSuccess(event.bitmap)
            }
        }
    }

    private fun onCaptureSuccess(bitmap: Bitmap) {
        _state.update { it.copy(imageCapture = bitmap) }
    }
}
