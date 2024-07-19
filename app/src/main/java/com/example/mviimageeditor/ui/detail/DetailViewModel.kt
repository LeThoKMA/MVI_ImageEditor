package com.example.mviimageeditor.ui.detail

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.divide
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.utils.QUERY_SEARCH
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DetailViewModel() : BaseViewModel(), DetailContract {
    private val _state = MutableStateFlow(DetailContract.State())
    private val _effect = MutableSharedFlow<DetailContract.Effect>()
    override val state: StateFlow<DetailContract.State>
        get() = _state
    override val effect: SharedFlow<DetailContract.Effect>
        get() = _effect

    override fun event(event: DetailContract.Event) {
        when (event) {
            is DetailContract.Event.SelectColor -> {
                updateColor(event.color)
            }

            is DetailContract.Event.OnChangeEditState -> {
                updateEditState(event.editState)
            }

            is DetailContract.Event.UpdateDrawPath -> {
                updateDrawPath(event.drawPath)
            }

            is DetailContract.Event.AddDrawPath -> {
                addDrawPath()
            }
        }
    }

    private fun addDrawPath() {
        _state.update {
            it.copy(
                pathList = _state.value.pathList.apply {
                    add(
                        DrawPath(
                            path = Path(),
                            color = _state.value.selectedColor
                        )
                    )
                }
            )
        }
    }

    private fun updateDrawPath(drawPath: Path) {
    }

    private fun updateColor(color: Color) {
        _state.update {
            it.copy(selectedColor = color, pathList = _state.value.pathList.apply {
                add(
                    DrawPath(
                        path = Path(),
                        color = color
                    )
                )
            })
        }
    }

    private fun updateEditState(editState: EditState) {
        if (editState == EditState.ERASER) {
            _state.update {
                it.copy(
                    selectedColor = Color.Transparent,
                    editState = editState
                )
            }
        } else {
            _state.update {
                it.copy(editState = editState)
            }
        }


    }
}