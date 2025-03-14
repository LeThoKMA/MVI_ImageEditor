package com.example.mviimageeditor.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Offset
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class ImageAnalyzer(
    private val widthSize: Int,
    private val heightSize: Int,
    private val onUpdateUI: (Offset) -> Unit,
    private val onGone: () -> Unit,
) : ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val scaleWidth = widthSize.div(mediaImage?.width?.toFloat() ?: widthSize.toFloat())
        val scaleHeight = heightSize.div(mediaImage?.height?.toFloat() ?: heightSize.toFloat())
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val options =
                FaceDetectorOptions
                    .Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) // Chính xác cao
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) // Nhận diện mốc (mắt, mũi, ...)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // Xác định trạng thái (mỉm cười, mở mắt, ...)
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL) // Lấy đường viền khuôn mặt
                    .enableTracking() // Bật theo dõi ID
                    .build()
            val result =
                FaceDetection
                    .getClient(options)
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
        scaleWidth: Float,
        scaleHeight: Float,
    ) {
        val face = faces.first()
        println(face.toString())
        // Xác định vị trí tai thỏ (dựa vào trán - top of face)
        val bounds = face.boundingBox // Lấy bounding box của khuôn mặt
        val rotY = face.headEulerAngleY // Lấy góc xoay theo trục Y,
        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
        if (leftEye != null && rightEye != null) {
            if (rotY >= -30 && rotY <= 30) {
//                val x = leftEye.x * scaleWidth
//                val y = leftEye.y * scaleHeight
                val x = bounds.left * scaleWidth + bounds.width() * scaleWidth / 2
                val y = bounds.top * scaleHeight
                onUpdateUI.invoke(Offset(x, y))
            } else {
                onGone.invoke()
            }
        }
    }
}
