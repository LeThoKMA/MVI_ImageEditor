package com.example.mviimageeditor.ui.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlin.math.abs

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDetail(
    source: Any?,
    modifier: Modifier = Modifier,
    isZoomable: Boolean = true,
    colorFilter: ColorFilter? = null,
) {
    val localConfiguration = LocalConfiguration.current
    val imgSize by
        remember {
            mutableStateOf(IntSize(localConfiguration.screenWidthDp, localConfiguration.screenHeightDp))
        }
    var scale by remember {
        mutableFloatStateOf(1f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    val modifier =
        modifier
            .fillMaxSize()
            .pointerInput(isZoomable) {
                if (isZoomable) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        scale *= zoom
                        if (scale > 1) {
                            offset =
                                offsetChange(offset, scale, pan, zoom, imgSize)
                        }
                    }
                }
            }.onZoomAndDrag(scale, offset)

    if (source is String) {
        GlideImage(
            model = source,
            contentDescription = "",
            modifier = modifier,
            colorFilter = colorFilter,
            contentScale = Crop,
        )
    } else {
        Image(
            painter = source as BitmapPainter,
            contentDescription = "",
            modifier = modifier,
            contentScale = Fit,
            colorFilter = colorFilter,
        )
    }
}

private fun Modifier.onZoomAndDrag(
    scale: Float,
    offset: Offset,
) = this then
    Modifier.graphicsLayer {
        scaleX = maxOf(scale, 1f)
        scaleY = maxOf(scale, 1f)
        translationX = if (scale > 1) offset.x else 0f
        translationY = if (scale > 1) offset.y else 0f
    }

private fun offsetChange(
    offset: Offset,
    scale: Float,
    pan: Offset,
    zoom: Float,
    imgSize: IntSize,
): Offset {
    val anchorX = imgSize.width.times(abs(scale - 1)) / 2
    val anchorY = imgSize.height.times(abs(scale - 1)) / 2
    val offsetX =
        if (offset.x + pan.x * zoom < 0) {
            maxOf(
                offset.x + pan.x * zoom,
                -anchorX,
            )
        } else {
            minOf(
                offset.x + pan.x * zoom,
                anchorX,
            )
        }
    val offsetY =
        if (offset.y + pan.y * zoom < 0) {
            maxOf(
                offset.y + pan.y * zoom,
                -anchorY,
            )
        } else {
            minOf(
                offset.y + pan.y * zoom,
                anchorY,
            )
        }
    return Offset(offsetX, offsetY)
}
