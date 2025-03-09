package com.example.mviimageeditor.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Offset
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceLandmark

class ImageAnalyzer(private val onUpdateUI: (Offset) -> Unit, private val onGone: () -> Unit) :
    ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val result = FaceDetection.getClient().process(image)
                .addOnSuccessListener { faces ->
                    // Xử lý kết quả nhận diện khuôn mặt
                    if (faces.isNotEmpty()) {
                        addImageOnFace(faces)
                    } else {
                        onGone.invoke()
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
            result.addOnCanceledListener(onGone)
        }
    }

    private fun addImageOnFace(faces: List<Face>) {
        val face = faces.first()

        // Xác định vị trí tai thỏ (dựa vào trán - top of face)
        val bounds = face.boundingBox // Lấy bounding box của khuôn mặt
        val rotY = face.headEulerAngleY // Lấy góc xoay theo trục Y,
        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position

        //if (leftEye != null && rightEye != null) {
        if (rotY >= -30 && rotY <= 30) {
//        val centerX = (leftEye.x + rightEye.x) / 2
//        val centerY = (leftEye.y + rightEye.y) / 2 - 150
            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()
            onUpdateUI.invoke(Offset(centerX, centerY))
        } else {
            onGone.invoke()
        }
    }
}