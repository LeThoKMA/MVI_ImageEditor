package com.example.mviimageeditor.camera

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset

@Stable
data class FaceAnalysisUIState(
    val offsetView: IntOffset = IntOffset.Zero,
    val viewSize: Pair<Int, Int>? = null,
    val headEulerAngleX: Float? = null,
    val headEulerAngleY: Float? = null,
    val headEulerAngleZ: Float? = null,
)
