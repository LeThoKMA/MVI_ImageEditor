package com.example.mviimageeditor.camera

import android.graphics.PointF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.unit.IntOffset
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class ImageAnalyzer(
    private val widthSize: Int,
    private val heightSize: Int,
    private val onUpdateUI: (ImageAnalystModel) -> Unit,
    private val onGone: () -> Unit,
) : ImageAnalysis.Analyzer {
    private val options by lazy {
        FaceDetectorOptions
            .Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) // Chính xác cao
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) // Nhận diện mốc (mắt, mũi, ...)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // Xác định trạng thái (mỉm cười, mở mắt, ...)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL) // Lấy đường viền khuôn mặt
            .enableTracking() // Bật theo dõi ID
            .build()
    }

    private val faceDetection by lazy {
        FaceDetection
            .getClient(options)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val scaleWidth = widthSize.div(mediaImage?.width ?: widthSize)
        val scaleHeight = heightSize.div(mediaImage?.height ?: heightSize)
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val result =
                faceDetection
                    .process(image)
                    .addOnSuccessListener { faces ->
                        // Xử lý kết quả nhận diện khuôn mặt
                        if (faces.isNotEmpty()) {
                            addImageOnFace(faces, scaleWidth, scaleHeight)
                        } else {
                            onGone.invoke()
                        }
                    }.addOnCompleteListener {
                        imageProxy.close()
                    }
            result.addOnCanceledListener(onGone)
        }
    }

    private fun addImageOnFace(
        faces: List<Face>,
        scaleWidth: Int,
        scaleHeight: Int,
    ) {
        val face = faces.first()
        // Xác định vị trí tai thỏ (dựa vào trán - top of face)
        val bounds = face.boundingBox // Lấy bounding box của khuôn mặt

        val rotY = face.headEulerAngleY // Lấy góc xoay theo trục Y,
        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
        if (leftEye != null && rightEye != null) {
//            if (rotY >= -30 && rotY <= 30) {
//                onUpdateUI.invoke(
//                    ImageAnalystModel(
//                        faceBoundingBox = Pair(bounds.width(), bounds.height()),
//                        leftEye,
//                        rightEye,
//                    ),
//                )
//            } else {
//                onGone.invoke()
//            }
            onUpdateUI.invoke(
                ImageAnalystModel(
                    faceBoundingBox = Pair(bounds.width(), bounds.height()),
                    headEulerAngleX = face.headEulerAngleX,
                    headEulerAngleY = face.headEulerAngleY,
                    headEulerAngleZ = face.headEulerAngleZ,
                    leftEye,
                    rightEye,
                ),
            )
        }
    }
}

data class ImageAnalystModel(
    val faceBoundingBox: Pair<Int, Int>? = null,
    val headEulerAngleX: Float? = null,
    val headEulerAngleY: Float? = null,
    val headEulerAngleZ: Float? = null,
    private val leftEye: PointF? = null,
    private val rightEye: PointF? = null,
) {
    val offsetFilterView: IntOffset
        get() {
            val xLeft = leftEye?.x?.toInt() ?: 0
            val y = leftEye?.y?.toInt()?.minus(faceBoundingBox?.first?.div(2) ?: 0) ?: 0
            val xRight = rightEye?.x?.toInt() ?: 0
            val x = ((xLeft + xRight) / 2).minus(faceBoundingBox?.first?.div(2) ?: 0)
            return IntOffset(x, y)
        }
}
