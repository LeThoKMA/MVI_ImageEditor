package com.example.mviimageeditor.custom

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mviimageeditor.ui.detail.DetailContract
import kotlinx.coroutines.launch

@Composable
fun CropView(
    modifier: Modifier,
    onDrag1stQuad: (Offset) -> Unit,
    onDrag2ndQuad: (Offset) -> Unit,
    onDrag3rdQuad: (Offset) -> Unit,
    onDrag4thQuad: (Offset) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopStart)
                .pointerInput(key1 = onDrag2ndQuad) {
                    detectDragGestures(
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            change.consume()
                            onDrag2ndQuad.invoke(dragAmount)
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(0f, height)  // Điểm bắt đầu của đường đầu tiên
                lineTo(0f, height / 2)  // Đường ngang đầu tiên
                quadraticTo(0f, 0f, width / 2, 0f) // Đường cong Bézier để tạo bo góc
                lineTo(width, 0f)  // Đường thẳng đứng thứ hai
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = 20f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopEnd)
                .pointerInput(key1 = onDrag1stQuad) {
                    detectDragGestures(
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            onDrag1stQuad.invoke(dragAmount)
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(width, height)  // Điểm bắt đầu của đường đầu tiên
                lineTo(width, height / 2)  // Đường ngang đầu tiên
                quadraticTo(
                    width,
                    0f,
                    width / 2,
                    0f
                ) // Đường cong Bézier để tạo bo góc
                lineTo(0f, 0f)  // Đường thẳng đứng thứ hai
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = 20f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }


        Canvas(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomEnd)
                .pointerInput(key1 = onDrag4thQuad) {
                    detectDragGestures(
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            onDrag4thQuad.invoke(dragAmount)
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(0f, height)  // Điểm bắt đầu của đường đầu tiên
                lineTo(width / 2, height)  // Đường ngang đầu tiên
                quadraticTo(
                    width,
                    height,
                    width,
                    height / 2
                ) // Đường cong Bézier để tạo bo góc
                lineTo(width, 0f)  // Đường thẳng đứng thứ hai
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = 20f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        Canvas(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomStart)
                .pointerInput(key1 = onDrag3rdQuad) {
                    detectDragGestures(
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            onDrag3rdQuad.invoke(dragAmount)
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(0f, 0f)  // Điểm bắt đầu của đường đầu tiên
                lineTo(0f, height / 2)  // Đường ngang đầu tiên
                quadraticTo(
                    0f,
                    height,
                    width / 2,
                    height
                ) // Đường cong Bézier để tạo bo góc
                lineTo(width, height)  // Đường thẳng đứng thứ hai
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = 20f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }

}

