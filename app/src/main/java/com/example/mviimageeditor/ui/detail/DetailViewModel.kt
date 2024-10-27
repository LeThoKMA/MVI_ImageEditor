package com.example.mviimageeditor.ui.detail

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewModelScope
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.repository.detail.DetailRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(private val detailRepository: DetailRepository) : BaseViewModel(),
    DetailContract {
    private val _state = MutableStateFlow(DetailContract.State())
    private val _effect = MutableSharedFlow<DetailContract.Effect>()
    override val state: StateFlow<DetailContract.State>
        get() = _state
    override val effect: SharedFlow<DetailContract.Effect>
        get() = _effect.asSharedFlow()

    override fun event(event: DetailContract.Event) {
        when (event) {
            is DetailContract.Event.SelectColor -> {
                updateColor(event.color)
            }

            is DetailContract.Event.OnChangeEditState -> {
                updateEditState(event.editState)
            }

            is DetailContract.Event.DownloadImage -> {
                downloadImage(event.source)
            }

            is DetailContract.Event.SaveImageCrop -> {
                saveImageCrop(event.source, event.topLeft, event.bottomRight)
            }
        }
    }

    private fun saveImageCrop(source: ImageBitmap, topLeft: Offset, bottomRight: Offset) {
        viewModelScope.launch(IO) {
            // Calculate width and height from topLeft and bottomRight
            val width = (bottomRight.x - topLeft.x).toInt()
            val height = (bottomRight.y - topLeft.y).toInt()

            val imageCrop = BitmapPainter(
                source,
                IntOffset(
                    topLeft.x.toInt(), topLeft.y.toInt()
                ),
                IntSize(width, height)
            )
            _state.update {
                it.copy(imageCrop = imageCrop, editState = EditState.DONE)
            }
        }
    }

    private fun clearData() {
        _state.update {
            it.copy(pathList = _state.value.pathList.apply {
                clear()
                add(
                    DrawPath(
                        path = Path(),
                        color = _state.value.selectedColor
                    )
                )
            }, selectedColor = Color.Unspecified, editState = EditState.NONE)
        }
    }

    private fun downloadImage(bitmap: ImageBitmap) {
        viewModelScope.launch {
            detailRepository.saveImage(bitmap.asAndroidBitmap())
                .catch {
                    handleApiError(it)
                }.onCompletion {
                    clearData()
                    _effect.emit(DetailContract.Effect.ShowToast("Image Saved"))
                }.collect {}
        }
    }

    private fun updateColor(color: Color) {
        viewModelScope.launch(IO) {
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
    }

    private fun updateEditState(editState: EditState) {
        when (editState) {
            EditState.DRAW -> {
                _state.update {
                    it.copy(editState = editState, selectedColor = Color.Red,
                        pathList = _state.value.pathList.apply {
                            add(
                                DrawPath(
                                    path = Path(),
                                    color = Color.Red,
                                    blendMode = BlendMode.SrcOver
                                )
                            )
                        })
                }
            }

            EditState.ERASER -> {
                _state.update {
                    it.copy(
                        selectedColor = Color.Transparent,
                        editState = editState, pathList = _state.value.pathList.apply {
                            add(
                                DrawPath(
                                    path = Path(),
                                    color = Color.Transparent,
                                    strokeWidth = 30f,
                                    blendMode = BlendMode.Clear
                                )
                            )
                        }
                    )
                }
            }

            EditState.DONE -> {
                _state.update {
                    it.copy(editState = editState)
                }
            }

            else -> {
                _state.update {
                    it.copy(editState = editState)
                }
            }
        }
    }
}