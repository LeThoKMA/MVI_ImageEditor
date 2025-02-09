package com.example.mviimageeditor.ui.detail

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.example.mviimageeditor.ContractViewModel

interface DetailContract : ContractViewModel<DetailContract.State, DetailContract.Event, DetailContract.Effect> {
    @Stable
    data class State(
        val imageUrl: String = "",
        val colorList: List<Color> =
            listOf(
                Color.Red,
                Color.Green,
                Color.Blue,
                Color.Yellow,
                Color.Magenta,
                Color.Cyan,
                Color.Transparent,
            ),
        val editState: EditState = EditState.NONE,
        val selectedColor: Color = Color.Red,
        val pathList: MutableList<DrawPath> = mutableListOf(DrawPath(Path(), Color.Red)),
        val imageCrop: BitmapPainter? = null,
        val colorFilters: List<ColorFilter> = listOf(),
    )

    sealed class Event {
        data class SelectColor(
            val color: Color,
        ) : Event()

        data class OnChangeEditState(
            val editState: EditState,
        ) : Event()

        data class DownloadImage(
            val source: ImageBitmap,
        ) : Event()

        data class SaveImageCrop(
            val source: ImageBitmap,
            val topLeft: Offset,
            val bottomRight: Offset,
        ) : Event()
    }

    sealed class Effect {
        data class ShowToast(
            val message: String,
        ) : Effect()
    }
}

data class DrawPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float = 10f,
    val blendMode: BlendMode = BlendMode.SrcOver,
)

enum class EditState {
    NONE,
    DRAW,
    ERASER,
    CROP,
    FILTER,
    DONE,
    CLEAR,
}
