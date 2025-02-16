package com.example.mviimageeditor.ui.create

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mviimageeditor.permission.PermissionRequester
import com.example.mviimageeditor.ui.create.component.CameraOptionView
import com.example.mviimageeditor.use
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateScreen(
    modifier: Modifier = Modifier,
    viewmodel: CaptureImageViewmodel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val (state, event, effect) = use(viewmodel)
    val cameraHelper =
        remember {
            CameraHelper(context, lifecycleOwner)
        }

    val permissions = remember {
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
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                this.data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        }
    }

    Box(modifier) {
        if (state.imageCapture == null) {
            cameraHelper.surfaceRequest?.let {
                CaptureView(
                    surfaceRequest = it,
                    event = event,
                )
            }
        }
        state.imageCapture?.let {
            Image(it.asImageBitmap(), contentDescription = "Captured Image")
        }
    }
}

@Composable
fun BoxScope.CaptureView(
    surfaceRequest: SurfaceRequest,
    event: (CaptureImageContract.Event) -> Unit,
) {
    CameraXViewfinder(
        surfaceRequest = surfaceRequest,
        modifier = Modifier.fillMaxSize(),
    )

    CameraOptionView(
        Modifier.align(Alignment.BottomCenter),
        onCapture = {
            event(CaptureImageContract.Event.OnCapture)
            println(">>>>>>>>>>>>>>")
        },
        onFlash = { event(CaptureImageContract.Event.OnFlash) },
        onSwitchCamera = { event(CaptureImageContract.Event.OnSwitchCamera) },
    )
}

