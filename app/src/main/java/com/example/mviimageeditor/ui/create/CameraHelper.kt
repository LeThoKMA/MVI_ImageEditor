package com.example.mviimageeditor.ui.create

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class CameraHelper(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
) {
    private var _surfaceRequest by mutableStateOf<SurfaceRequest?>(null)
    val surfaceRequest get() = _surfaceRequest

    private var isUsingFrontCamera = true
    private val cameraPreviewUseCase =
        Preview.Builder().build().apply {
            setSurfaceProvider { newSurfaceRequest ->
                _surfaceRequest = newSurfaceRequest
            }
        }
    private val imageCapture = ImageCapture.Builder().build()

    init {
        bindToCamera()
    }

    private fun bindToCamera() {
        lifecycleOwner.lifecycleScope.launch {
            val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
            processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                DEFAULT_FRONT_CAMERA,
                imageCapture,
                cameraPreviewUseCase,
            )

            // Cancellation signals we're done with the camera
            try {
                awaitCancellation()
            } finally {
                processCameraProvider.unbindAll()
            }
        }
    }


    fun onCaptureImage(
        onPhotoCaptured: (Bitmap) -> Unit,
    ) {
        val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

        imageCapture.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val correctedBitmap: Bitmap = image
                    .toBitmap()
//                    .rotateBitmap(image.imageInfo.rotationDegrees)

                onPhotoCaptured(correctedBitmap)
                image.close()

            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraContent", "Error capturing image", exception)
            }
        })
    }

    fun onSwitchCamera() {
        lifecycleOwner.lifecycleScope.launch {
            val processCameraProvider = ProcessCameraProvider.awaitInstance(context)

            // Xác định camera cần dùng
            val newCameraSelector =
                if (isUsingFrontCamera) {
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                } else {
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build()
                }

            // Unbind tất cả camera trước khi bind lại
            processCameraProvider.unbindAll()

            processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                newCameraSelector,
                cameraPreviewUseCase,
                imageCapture
            )

            // Cập nhật trạng thái camera
            isUsingFrontCamera = !isUsingFrontCamera
        }
    }
}
