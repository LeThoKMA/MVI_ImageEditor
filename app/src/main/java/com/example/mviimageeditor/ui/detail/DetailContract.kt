package com.example.mviimageeditor.ui.detail

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
        val pathList: MutableList<DrawPath> = mutableListOf(DrawPath(Path(), Color.Red))
    )

    sealed class Event {
        data class SelectColor(val color: Color) : Event()
        data class OnChangeEditState(val editState: EditState) : Event()

        data class DownloadImage(val source: ImageBitmap) : Event()

        data object AddDrawPath : Event()

    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }

}

data class DrawPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float = 10f,
    val blendMode: BlendMode = BlendMode.SrcOver
)

enum class EditState {
    NONE,
    DRAW,
    ERASER,
    CROP,
    FILTER,
    DONE,
    CLEAR
}