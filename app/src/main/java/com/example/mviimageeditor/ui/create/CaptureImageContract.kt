package com.example.mviimageeditor.ui.create

import com.example.mviimageeditor.ContractViewModel

sealed interface CaptureImageContract :
    ContractViewModel<CaptureImageContract.State, CaptureImageContract.Event, CaptureImageContract.Effect> {
    data class State(
        val imageCapture: android.graphics.Bitmap? = null,
    )

    sealed class Event {
        data object OnSwitchCamera : Event()

        data object OnCapture : Event()

        data object OnFlash : Event()

        data class OnCaptureSuccess(val bitmap: android.graphics.Bitmap) : Event()
    }

    sealed class Effect {
        data object CaptureImage : Effect()

        data object FlashImage : Effect()

        data object SwitchCamera : Effect()
    }
}
