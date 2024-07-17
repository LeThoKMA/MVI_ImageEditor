package com.example.mviimageeditor.ui.detail

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
            Color.Cyan,
            Color.Transparent
        ),
        @Stable
        val editState: EditState = EditState.NONE,
        val selectedColor: Color = Color.Red,
        val pathList: MutableList<DrawPath> = colorList.map { DrawPath(Path(), it) }
            .toMutableList(),
    )

    sealed class Event {
        data class SelectColor(val color: Color) : Event()
        data class OnChangeEditState(val editState: EditState) : Event()

        data class UpdateDrawPath(val drawPath: Path, val sourcePathIndex: Int) : Event()

    }

    sealed class Effect()

}

data class DrawPath(var path: Path, val color: Color)

enum class EditState {
    NONE,
    DRAW,
    ERASER,
    CROP,
    FILTER,
    DONE
}