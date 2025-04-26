package com.example.mviimageeditor.ui.ar

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun FilamentView(
    modifier: Modifier,
    isShow: Boolean,
) {
    // Box(modifier = Modifier.zIndex(1f).fillMaxSize()) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ModelViewerView(context)
        },
        update = {
            it.visibility = if (isShow) View.VISIBLE else View.GONE
//            it.setZOrderOnTop(true)
//            it.setZOrderMediaOverlay(true)
        },
    )
    // }
}
