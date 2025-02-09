package com.example.mviimageeditor.ui.detail

import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mviimageeditor.R
import com.example.mviimageeditor.custom.CropView
import com.example.mviimageeditor.ui.detail.component.ImageFilterList
import com.example.mviimageeditor.ui.detail.component.ItemOption
import com.example.mviimageeditor.ui.theme.GrayE0
import com.example.mviimageeditor.use
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(detailViewModel: DetailViewModel = koinViewModel()) {
    val (state, event, effect) = use(viewModel = detailViewModel)
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var scale by remember { mutableFloatStateOf(1f) }
    val scaleDerived by remember {
        derivedStateOf {
            if (scale > 1) {
                scale
            } else {
                1f
            }
        }
    }

    var offset by remember { mutableStateOf(Offset.Zero) }

    var isExpanded by remember { mutableStateOf(false) }

    var imageSize by remember { mutableStateOf(IntSize.Zero) }

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

    val onColorFilterChange: (ColorFilter) -> Unit = {
        colorFilter = it
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
                    .animateContentSize()
                    .drawWithContent {
                        // call record to capture the content in the graphics layer
                        graphicsLayer.record {
                            // draw the contents of the composable into the graphics layer
                            this@drawWithContent.drawContent()
                        }
                        // draw the graphics layer on the visible canvas
                        drawLayer(graphicsLayer)
                    }.combinedClickable(
                        onClick = { },
                        onDoubleClick = {
                            isExpanded = !isExpanded
                        },
                        onLongClick = {
                        },
                    ).pointerInput(key1 = state.editState) {
                        if (state.editState == EditState.DONE || state.editState == EditState.NONE) {
                            detectTransformGestures { centroid, pan, zoom, _ ->
                                scale *= zoom
                                if (scale > 1) {
                                    offset =
                                        offsetChange(offset, scale, pan, zoom, imageSize)
                                }
                            }
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
            GlideImage(
                model = state.imageUrl,
                contentDescription = "",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scaleDerived,
                            scaleY = scaleDerived,
                            translationX = if (scale > 1) offset.x else 0f,
                            translationY = if (scale > 1) offset.y else 0f,
                        ).onGloballyPositioned { layoutCoordinates ->
                            imageSize = layoutCoordinates.size
                        },
                colorFilter = colorFilter,
                contentScale = Crop,
            )

            if (state.imageCrop != null) {
                Image(
                    painter = state.imageCrop,
                    contentDescription = "",
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scaleDerived,
                                scaleY = scaleDerived,
                                translationX = if (scale > 1) offset.x else 0f,
                                translationY = if (scale > 1) offset.y else 0f,
                            ).background(Color.Black),
                    contentScale = Fit,
                )
            }

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
                        },
            ) {
                if (point != Offset(0f, 0f) && state.editState != EditState.ERASER) {
                    drawPath(
                        path = drawPath.path,
                        color = drawPath.color,
                        style = Stroke(drawPath.strokeWidth),
                        blendMode = drawPath.blendMode,
                    )
                }
                if (state.editState == EditState.ERASER) {
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
                        Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth()
                            .align(Alignment.BottomStart),
                    imageSource = state.imageCrop ?: state.imageUrl,
                    colorFilters = state.colorFilters,
                ) {
                    onColorFilterChange(it)
                }
            }
        }
        if (state.editState != EditState.CROP) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .zIndex(2f),
            ) {
                Column(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .animateContentSize()
                            .padding(end = 8.dp, top = 32.dp),
                ) {
                    if (state.editState != EditState.DONE && state.editState != EditState.NONE) {
                        Text(
                            text = "DONE",
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier =
                                Modifier.clickable {
                                    event.invoke(
                                        DetailContract.Event.OnChangeEditState(EditState.DONE),
                                    )
                                },
                        )
                    }
                    IconButton(
                        onClick = { event.invoke(DetailContract.Event.OnChangeEditState(EditState.DRAW)) },
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "draw",
                            modifier = Modifier.size(40.dp),
                            tint = GrayE0,
                        )
                    }

                    Canvas(
                        modifier =
                            Modifier.size(40.dp).graphicsLayer {
                                compositingStrategy = CompositingStrategy.Offscreen
                            },
                        contentDescription = "",
                    ) {
                        val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_eraser)
                        drawImage(
                            drawable!!.toBitmap().asImageBitmap(),
                            blendMode = BlendMode.Overlay,
                        )
                    }

//                    ItemOption(icon = R.drawable.ic_eraser) {
//                        event.invoke(
//                            DetailContract.Event.OnChangeEditState(
//                                EditState.ERASER,
//                            ),
//                        )
//                    }

                    if (state.editState == EditState.NONE || state.editState == EditState.DONE) {
                        ItemOption(icon = R.drawable.icon_crop) {
                            event.invoke(
                                DetailContract.Event.OnChangeEditState(
                                    EditState.CROP,
                                ),
                            )
                        }
                        ItemOption(icon = R.drawable.icon_filter, color = Color.White) {
                            event.invoke(
                                DetailContract.Event.OnChangeEditState(
                                    EditState.FILTER,
                                ),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                    if (state.editState == EditState.DRAW) {
                        state.colorList.forEachIndexed { index, color ->
                            RadioButton(
                                selected = state.selectedColor == color,
                                onClick = { event.invoke(DetailContract.Event.SelectColor(color)) },
                                modifier =
                                    Modifier
                                        .background(Color.Transparent)
                                        .padding(10.dp),
                                colors =
                                    RadioButtonDefaults.colors(
                                        selectedColor = color,
                                        unselectedColor = color,
                                    ),
                            )
                        }
                    }
                }
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
}

fun offsetChange(
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
