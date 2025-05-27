package com.example.mviimageeditor.ui.ar

import ModelTextureView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun FilamentView(
    modifier: Modifier = Modifier,
    useTextureView: Boolean = false,
    testMode: Boolean = false,
    useTransparentSurface: Boolean = false,
//    isShow: Boolean,
//    headEulerAngleX: Float? = null,
//    headEulerAngleY: Float? = null,
//    headEulerAngleZ: Float? = null,
) {
    AndroidView(
        modifier = modifier,
        factory = ::ModelTextureView,
        update = { view ->
//            view.visibility = if (isShow) View.VISIBLE else View.GONE
//            if (headEulerAngleX != null && headEulerAngleY != null && headEulerAngleZ != null) {
//
//            }
        },
    )
}
