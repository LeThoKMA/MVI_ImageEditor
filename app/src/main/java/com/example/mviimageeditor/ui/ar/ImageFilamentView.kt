package com.example.mviimageeditor.ui.ar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun FilamentView(
    modifier: Modifier = Modifier,
//    isShow: Boolean,
//    headEulerAngleX: Float? = null,
//    headEulerAngleY: Float? = null,
//    headEulerAngleZ: Float? = null,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ModelViewerView(context).apply {
                s
            }
        },
        update = {
//            it.visibility = if (isShow) View.VISIBLE else View.GONE
//            if (headEulerAngleX != null && headEulerAngleY != null && headEulerAngleZ != null) {
//                it.setRotation(headEulerAngleX, headEulerAngleY, headEulerAngleZ)
//            }
        },
    )
}
