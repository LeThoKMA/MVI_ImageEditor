package com.example.mviimageeditor.ui.create

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mviimageeditor.camera.CameraHelper
import com.example.mviimageeditor.permission.PermissionRequester
import com.example.mviimageeditor.ui.ar.FilamentView
import com.example.mviimageeditor.ui.create.component.CameraOptionView
import com.example.mviimageeditor.use
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CreateScreen(viewmodel: CaptureImageViewmodel = koinViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val windowInfo = LocalWindowInfo.current.containerSize

    val (state, event, effect) = use(viewmodel)
    val cameraHelper =
        remember {
            CameraHelper(
                context,
                lifecycleOwner,
                widthSize = windowInfo.width,
                heightSize = windowInfo.height,
            )
        }
    val faceAnalysisUIState by cameraHelper.faceAnalysisUiState.collectAsStateWithLifecycle()
    val isShowFilter by remember(faceAnalysisUIState.offsetView) {
        derivedStateOf {
            IntOffset.Zero != faceAnalysisUIState.offsetView
        }
    }
    val faceWidth = remember(faceAnalysisUIState.viewSize) { faceAnalysisUIState.viewSize?.first }
    val faceHeight = remember(faceAnalysisUIState.viewSize) { faceAnalysisUIState.viewSize?.second }

    val permissions =
        remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                )
            } else {
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            }
        }

    LaunchedEffect(effect) {
        effect.collectLatest {
            when (it) {
                CaptureImageContract.Effect.CaptureImage -> {
                    cameraHelper.onCaptureImage { bitmap ->
                        event.invoke(CaptureImageContract.Event.OnCaptureSuccess(bitmap))
                    }
                }

                CaptureImageContract.Effect.FlashImage -> {}
                CaptureImageContract.Effect.SwitchCamera -> {
                    cameraHelper.onSwitchCamera()
                }
            }
        }
    }

    PermissionRequester(permissions) { data ->
        if (data.filter { !it.value }.isNotEmpty()) {
            val intent =
                Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    this.data = Uri.parse("package:${context.packageName}")
                }
            context.startActivity(intent)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.imageCapture == null) {
            CaptureView(
                event = event,
            )
        }

        // if (faceWidth != null && faceHeight != null) {
        FilamentView(
            isShow = true,
            modifier =
                Modifier.fillMaxSize(),
//                        .offset {
//                            faceAnalysisUIState.offsetView
//                        }.size(width = faceWidth.dp, height = faceHeight.dp),
//                isShow = isShowFilter,
//                headEulerAngleX = faceAnalysisUIState.headEulerAngleX,
//                headEulerAngleY = faceAnalysisUIState.headEulerAngleY,
//                headEulerAngleZ = faceAnalysisUIState.headEulerAngleZ,
        )
    }
    // }

    state.imageCapture?.let {
        Image(
            it.asImageBitmap(),
            contentDescription = "Captured Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CaptureView(event: (CaptureImageContract.Event) -> Unit) {
    Box(
        modifier =
            Modifier.fillMaxSize(),
    ) {
        CameraPreviewView()

        CameraOptionView(
            Modifier.align(Alignment.BottomCenter),
            onCapture = {
                event(CaptureImageContract.Event.OnCapture)
            },
            onFlash = { event(CaptureImageContract.Event.OnFlash) },
            onSwitchCamera = { event(CaptureImageContract.Event.OnSwitchCamera) },
        )
    }
}

@Composable
fun CameraPreviewView(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = modifier,
    ) { view ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview =
                Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                )
            } catch (e: Exception) {
                Log.e("CameraPreviewView", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
