package com.example.mviimageeditor.camera

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset

@Stable
data class FaceAnalysisUIState(
    val offsetView: IntOffset = IntOffset.Zero,
    val viewSize: Pair<Int, Int>? = null,
)
