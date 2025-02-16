package com.example.mviimageeditor.ui.detail

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.mviimageeditor.custom.CropView
import com.example.mviimageeditor.ui.detail.component.ImageDetail
import com.example.mviimageeditor.ui.detail.component.ImageFilterList
import com.example.mviimageeditor.ui.detail.component.OptionView
import com.example.mviimageeditor.use
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(detailViewModel: DetailViewModel = koinViewModel()) {
    val (state, event, effect) = use(viewModel = detailViewModel)
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var drawPath by remember {
        mutableStateOf(
            state.pathList.last(),
        )
    }
    var point by remember {
        mutableStateOf(Offset.Zero)
    }
    var colorFilter by remember {
        mutableStateOf<ColorFilter?>(null)
    }
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    val onColorFilterChange =
        remember<(ColorFilter) -> Unit> {
            { colorFilter = it }
        }

    LaunchedEffect(key1 = state.selectedColor) {
        drawPath = state.pathList.last()
        point = Offset(0f, 0f)
    }

    LaunchedEffect(key1 = effect) {
        effect.collectLatest {
            when (it) {
                is DetailContract.Effect.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        // call record to capture the content in the graphics layer
                        graphicsLayer.record {
                            // draw the contents of the composable into the graphics layer
                            this@drawWithContent.drawContent()
                        }
                        // draw the graphics layer on the visible canvas
                        drawLayer(graphicsLayer)
                    },
        ) {
            ImageDetail(
                source = state.imgDest,
                modifier = Modifier,
                isZoomable = state.editState == EditState.DONE || state.editState == EditState.NONE,
                colorFilter = colorFilter,
            )

            Canvas(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                        }.drawWithContent {
                            drawContent()
                            state.pathList.forEach {
                                drawPath(
                                    it.path,
                                    it.color,
                                    style = Stroke(it.strokeWidth),
                                    blendMode = it.blendMode,
                                )
                            }
                        }.pointerInput(key1 = state.editState) {
                            if (state.editState == EditState.DRAW || state.editState == EditState.ERASER) {
                                detectDragGestures(onDragStart = {
                                    point = it
                                    drawPath.path.moveTo(it.x, it.y)
                                }, onDragEnd = {
                                    drawPath.path.moveTo(point.x, point.y)
                                    drawPath.path.close()
                                }, onDragCancel = {
                                    drawPath.path.close()
                                }) { change, dragAmount ->
                                    point = change.position
                                    drawPath.path.apply {
                                        lineTo(point.x, point.y)
                                    }
                                }
                            }
                        },
            ) {
                if (point != Offset.Zero) {
                    drawPath(
                        path = drawPath.path,
                        color = drawPath.color,
                        style = Stroke(drawPath.strokeWidth),
                        blendMode = drawPath.blendMode,
                    )
                }
            }
            if (state.editState == EditState.CROP) {
                CropView(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                    screenWidth / 4,
                    screenHeight / 3,
                ) { topLeft, bottomRight ->
                    coroutineScope.launch {
                        val imageBitmap = graphicsLayer.toImageBitmap()
                        event.invoke(
                            DetailContract.Event.SaveImageCrop(
                                imageBitmap,
                                topLeft,
                                bottomRight,
                            ),
                        )
                    }
                }
            }
            if (state.editState == EditState.FILTER) {
                ImageFilterList(
                    modifier =
                        Modifier.align(Alignment.BottomStart),
                    imageSource = state.imgDest,
                    colorFilters = state.colorFilters,
                ) {
                    onColorFilterChange(it)
                }
            }
        }
        if (state.editState != EditState.CROP) {
            OptionView(state, event)
            if (state.editState == EditState.DONE || state.editState == EditState.NONE) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val bitmap = graphicsLayer.toImageBitmap()
                            event.invoke(DetailContract.Event.DownloadImage(bitmap))
                        }
                    },
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "save")
                }
            }
        }
    }
}
