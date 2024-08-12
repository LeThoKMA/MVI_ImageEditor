package com.example.mviimageeditor.custom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun CropView(
    modifier: Modifier,
    onDrag1stQuad: () -> Unit,
    onDrag2ndQuad: (Offset) -> Unit,
    onDrag3rdQuad: () -> Unit,
    onDrag4thQuad: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        Row(modifier = Modifier.size(100.dp).background(Color.Transparent).align(
            Alignment.Center
        )){
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .padding(top = 10.dp, start = 10.dp)

                    .pointerInput(key1 = onDrag2ndQuad) {
                        detectDragGestures(
                            onDrag = { change: PointerInputChange, dragAmount: Offset ->
                                onDrag2ndQuad.invoke(dragAmount)
//                            onDragDirection(
//                                dragAmount,
//                                onDragRight = {},
//                                onDragLeft = {},
//                                onDragTop = {},
//                                onDragBottom = {})
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
                        width = 10f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .padding(top = 10.dp, end = 10.dp)

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
                        width = 10f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }



        Box(modifier = Modifier.size(100.dp).background(Color.Transparent).align(
            Alignment.BottomEnd
        )){
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 10.dp, end = 10.dp)

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
                        width = 10f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        Box(modifier = Modifier.size(100.dp).background(Color.Transparent).align(
            Alignment.BottomStart
        )){
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 10.dp, start = 10.dp)

            ) {
                val width = size.width
                val height = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)  // Điểm bắt đầu của đường đầu tiên
                    lineTo(0f, height / 2)  // Đường ngang đầu tiên
                    quadraticTo(0f, height, width / 2, height) // Đường cong Bézier để tạo bo góc
                    lineTo(width, height)  // Đường thẳng đứng thứ hai
                }

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        width = 10f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }

}

inline fun onDragDirection(
    dragAmount: Offset,
    onDragRight: () -> Unit,
    onDragLeft: () -> Unit,
    onDragTop: () -> Unit,
    onDragBottom: () -> Unit,
) {
    when {
        dragAmount.x > 0 -> {
            // Kéo sang phải
            onDragRight()
        }

        dragAmount.x < 0 -> {
            // Kéo sang trái
            onDragLeft()
        }

        dragAmount.y > 0 -> {
            // Kéo xuống dưới
            onDragBottom()
        }

        dragAmount.y < 0 -> {
            // Kéo lên trên
            onDragTop()
        }
    }
}