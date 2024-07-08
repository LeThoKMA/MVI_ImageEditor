package com.example.mviimageeditor.ui.detail

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.example.mviimageeditor.ContractViewModel

interface DetailContract :
    ContractViewModel<DetailContract.State, DetailContract.Event, DetailContract.Effect> {
    data class State(
        @Stable
        val colorList: List<Color> = listOf(
            Color.Red,
            Color.Green,
            Color.Blue,
            Color.Yellow,
            Color.Magenta,
            Color.Cyan
        ),
        @Stable
        val editState: EditState = EditState.NONE,
        val selectedColor: Color = Color.Red
    )

    sealed class Event {
        data class SelectColor(val color: Color) : Event()
        data class OnChangeEditState(val editState: EditState) : Event()
    }

    sealed class Effect()

}

enum class EditState {
    NONE,
    DRAW,
    ERASER,
    CROP,
    FILTER,
    DONE
}