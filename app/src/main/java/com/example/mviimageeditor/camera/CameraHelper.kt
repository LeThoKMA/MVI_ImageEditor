package com.example.mviimageeditor.camera

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ImageAnalysis
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
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.mviimageeditor.utils.rotate
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraHelper(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val widthSize: Int,
    private val heightSize: Int,
) {
    private var _surfaceRequest by mutableStateOf<SurfaceRequest?>(null)
    val surfaceRequest get() = _surfaceRequest

    private val _faceAnalysisUiState = MutableStateFlow(FaceAnalysisUIState())
    val faceAnalysisUiState = _faceAnalysisUiState.asStateFlow()

    private var isUsingFrontCamera = true
    private val cameraPreviewUseCase =
        Preview.Builder().build().apply {
            setSurfaceProvider { newSurfaceRequest ->
                _surfaceRequest = newSurfaceRequest
            }
        }

    private val imageCapture = ImageCapture.Builder().build()
    private val analysisExecutor by lazy { Executors.newSingleThreadExecutor() }
    private val imageAnalyzer by lazy {
        val imageAnalysisUseCase =
            ImageAnalysis
                .Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
        imageAnalysisUseCase.setAnalyzer(
            analysisExecutor,
            ImageAnalyzer(
                widthSize = widthSize,
                heightSize = heightSize,
                onUpdateUI = { imageAnalyst ->
                    if (faceAnalysisUiState.value.viewSize == null && imageAnalyst.faceBoundingBox != null) {
                        _faceAnalysisUiState.update {
                            it.copy(
                                offsetView = imageAnalyst.offsetFilterView,
                                viewSize = imageAnalyst.faceBoundingBox,
                                headEulerAngleX = imageAnalyst.headEulerAngleX,
                                headEulerAngleY = imageAnalyst.headEulerAngleY,
                                headEulerAngleZ = imageAnalyst.headEulerAngleZ,
                            )
                        }
                    } else {
                        _faceAnalysisUiState.update {
                            it.copy(offsetView = imageAnalyst.offsetFilterView)
                        }
                    }
                },
                onGone = {
                    _faceAnalysisUiState.update {
                        it.copy(
                            offsetView = IntOffset.Zero,
//                            viewSize = null,
                        )
                    }
                },
            ),
        )
        imageAnalysisUseCase
    }

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
                imageAnalyzer,
            )

            // Cancellation signals we're done with the camera
            try {
                awaitCancellation()
            } finally {
                processCameraProvider.unbindAll()
            }
        }
    }

    fun onCaptureImage(onPhotoCaptured: (Bitmap) -> Unit) {
        val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

        imageCapture.takePicture(
            mainExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val correctedBitmap: Bitmap =
                        image
                            .toBitmap()
                            .rotate(image.imageInfo.rotationDegrees.toFloat())

                    onPhotoCaptured(correctedBitmap)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraContent", "Error capturing image", exception)
                }
            },
        )
    }

    fun onSwitchCamera() {
        lifecycleOwner.lifecycleScope.launch {
            val processCameraProvider = ProcessCameraProvider.awaitInstance(context)

            // Xác định camera cần dùng
            val newCameraSelector =
                if (isUsingFrontCamera) {
                    CameraSelector
                        .Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                } else {
                    CameraSelector
                        .Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build()
                }

            // Unbind tất cả camera trước khi bind lại
            processCameraProvider.unbindAll()

            processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                newCameraSelector,
                cameraPreviewUseCase,
                imageCapture,
            )

            // Cập nhật trạng thái camera
            isUsingFrontCamera = !isUsingFrontCamera
        }
    }
}
