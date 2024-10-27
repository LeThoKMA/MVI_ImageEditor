package com.example.mviimageeditor.custom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mviimageeditor.ui.theme.TransGray
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CropView(
    modifier: Modifier,
    defaultWidth: Dp,
    defaultHeight: Dp,
    onCropDone: (Offset, Offset) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val quadSize = remember {
        60.dp
    }
    val strokeWidth = remember {
        10f
    }

    val screenHeightPx = remember {
        with(density) {
            configuration.screenHeightDp.dp.toPx()
        }
    }
    val screenWidthPx = remember {
        with(density) {
            configuration.screenWidthDp.dp.toPx()
        }
    }
    val anchorWidth = remember {
        quadSize.value.plus(2)
    }
    val anchorHeight = remember {
        quadSize.value.plus(2)
    }
    var topLeftOffsetX by remember {
        mutableFloatStateOf(0f)
    }
    var topLeftOffsetY by remember {
        mutableFloatStateOf(0f)
    }
    var bottomRightOffsetX by remember {
        mutableFloatStateOf(0f)
    }
    var bottomRightOffsetY by remember {
        mutableFloatStateOf(0f)
    }
    var positionTopLeft by remember {
        mutableStateOf(Offset.Zero)
    }

    var positionBottomRight by remember {
        mutableStateOf(Offset.Zero)
    }

    var isVisibleDoneText by remember {
        mutableStateOf(true)
    }

    val onDragTopRight = remember<(Offset) -> Unit> {
        {
            val tmpPositionX = positionBottomRight.x + it.x
            val tmpPositionY = positionTopLeft.y + it.y

            if (abs(tmpPositionX - positionTopLeft.x) >= anchorWidth && tmpPositionX <= screenWidthPx) {
                bottomRightOffsetX += it.x
            }
            if (abs(tmpPositionY - positionBottomRight.y) >= anchorHeight && tmpPositionY >= 0) {
                topLeftOffsetY += it.y
            }
        }
    }
    val onDragTopLeft = remember<(Offset) -> Unit> {
        {
            val tmpPositionTopLeft = positionTopLeft + it
            if (abs(tmpPositionTopLeft.x - positionBottomRight.x) >= anchorWidth && tmpPositionTopLeft.x >= 0) {
                topLeftOffsetX += it.x
            }
            if (abs(tmpPositionTopLeft.y - positionBottomRight.y) >= anchorHeight && tmpPositionTopLeft.y >= 0) {
                topLeftOffsetY += it.y
            }
        }
    }

    val onDragBottomLeft = remember<(Offset) -> Unit> {
        {
            val tmpPositionX = positionTopLeft.x + it.x
            val tmpPositionY = positionBottomRight.y + it.y
            if (abs(tmpPositionX - positionBottomRight.x) >= anchorWidth && tmpPositionX >= 0) {
                topLeftOffsetX += it.x
            }
            if (abs(tmpPositionY - positionTopLeft.y) >= anchorHeight && tmpPositionY <= screenHeightPx) {
                bottomRightOffsetY += it.y
            }
        }
    }
    val onDragBottomRight = remember<(Offset) -> Unit> {
        {
            val tmpPositionBottomRight = positionBottomRight + it
            if (abs(tmpPositionBottomRight.x - positionTopLeft.x) >= anchorWidth && tmpPositionBottomRight.x <= screenWidthPx) {
                bottomRightOffsetX += it.x
            }
            if (abs(tmpPositionBottomRight.y - positionTopLeft.y) >= anchorHeight && tmpPositionBottomRight.y <= screenHeightPx) {
                bottomRightOffsetY += it.y
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        if (isVisibleDoneText) Text(text = "DONE", modifier = Modifier
            .align(Alignment.TopEnd)
            .clickable {
                onCropDone.invoke(positionTopLeft, positionBottomRight)
            })
        Canvas(
            modifier = Modifier
                .padding(top = defaultHeight, start = defaultWidth)
                .size(quadSize)
                .align(Alignment.TopStart)
                .zIndex(2f)
                .offset {
                    IntOffset(topLeftOffsetX.roundToInt(), topLeftOffsetY.roundToInt())
                }
                .onGloballyPositioned {
                    positionTopLeft = it
                        .positionInParent()
                        .plus(Offset(strokeWidth, strokeWidth))
                }
                .pointerInput(key1 = onDragTopLeft) {
                    detectDragGestures(
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            change.consume()
                            onDragTopLeft.invoke(dragAmount)
                        },
                        onDragEnd = {
                            isVisibleDoneText = true
                        },
                        onDragStart = {
                            isVisibleDoneText = false
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(0f, height.div(2))  // Điểm bắt đầu của đường đầu tiên
                lineTo(0f, 10f)  // Đường ngang đầu tiên
                quadraticTo(0f, 0f, 10f, 0f) // Đường cong Bézier để tạo bo góc
                lineTo(width.div(2), 0f)  // Đường thẳng đứng thứ hai
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        Canvas(
            modifier = Modifier
                .padding(top = defaultHeight, end = defaultWidth)
                .size(quadSize)
                .align(Alignment.TopEnd)
                .zIndex(2f)
                .offset {
                    IntOffset(
                        bottomRightOffsetX.roundToInt(),
                        topLeftOffsetY.roundToInt()
                    )
                }
                .pointerInput(key1 = onDragTopRight) {
                    detectDragGestures(
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            onDragTopRight.invoke(dragAmount)
                        },
                        onDragEnd = {
                            isVisibleDoneText = true
                        },
                        onDragStart = {
                            isVisibleDoneText = false
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(width.div(2), 0f)  // Điểm bắt đầu của đường đầu tiên
                lineTo(width - 10f, 0f)  // Đường ngang đầu tiên
                quadraticTo(
                    width,
                    0f,
                    width,
                    10f
                ) // Đường cong Bézier để tạo bo góc
                lineTo(width, height.div(2))  // Đường thẳng đứng thứ hai
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }


        Canvas(
            modifier = Modifier
                .padding(bottom = defaultHeight, end = defaultWidth)
                .size(quadSize)
                .align(Alignment.BottomEnd)
                .zIndex(2f)
                .offset {
                    IntOffset(
                        bottomRightOffsetX.roundToInt(),
                        bottomRightOffsetY.roundToInt()
                    )
                }
                .onGloballyPositioned {
                    val topLeftPoint = it.positionInParent()
                    positionBottomRight = Offset(
                        topLeftPoint.x + it.size.width,
                        topLeftPoint.y + it.size.height
                    ).minus(Offset(strokeWidth, strokeWidth))
                }
                .pointerInput(key1 = onDragBottomRight) {
                    detectDragGestures(
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            onDragBottomRight.invoke(dragAmount)
                        },
                        onDragEnd = {
                            isVisibleDoneText = true
                        },
                        onDragStart = {
                            isVisibleDoneText = false
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(width.div(2), height)  // Điểm bắt đầu của đường đầu tiên
                lineTo(width - 10f, height)  // Đường ngang đầu tiên
                quadraticTo(
                    width,
                    height,
                    width,
                    height - 10f
                ) // Đường cong Bézier để tạo bo góc
                lineTo(width, height.div(2))  // Đường thẳng đứng thứ hai
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        Canvas(
            modifier = Modifier
                .padding(bottom = defaultHeight, start = defaultWidth)
                .size(quadSize)
                .align(Alignment.BottomStart)
                .zIndex(2f)
                .offset {
                    IntOffset(
                        topLeftOffsetX.roundToInt(),
                        bottomRightOffsetY.roundToInt()
                    )
                }
                .pointerInput(key1 = onDragBottomLeft) {
                    detectDragGestures(
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            onDragBottomLeft.invoke(dragAmount)
                        },
                        onDragEnd = {
                            isVisibleDoneText = true
                        },
                        onDragStart = {
                            isVisibleDoneText = false
                        }
                    )
                }
        ) {
            val partWidth = size.width.div(2)
            val partHeight = size.height.div(2)
            val path = Path().apply {
                moveTo(0f, partHeight)  // Điểm bắt đầu của đường đầu tiên
                lineTo(0f, size.height - 10f)  // Đường ngang đầu tiên
                quadraticTo(
                    0f,
                    size.height,
                    10f,
                    size.height
                ) // Đường cong Bézier để tạo bo góc
                lineTo(partWidth, size.height)  // Đường thẳng đứng thứ hai
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        Canvas(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .zIndex(1f)) {
            // Vẽ nền ngoài hình chữ nhật
            val clipPath = Path().apply {
                addRect(
                    androidx.compose.ui.geometry.Rect(
                        topLeft = positionTopLeft,
                        bottomRight = positionBottomRight,
                    )
                )
            }
            // Cắt bỏ phần hình chữ nhật
            clipPath(clipPath, clipOp = ClipOp.Difference) {
                drawRect(
                    color = TransGray, // Màu nền ngoài
                    size = size
                )
            }
        }
    }
}



