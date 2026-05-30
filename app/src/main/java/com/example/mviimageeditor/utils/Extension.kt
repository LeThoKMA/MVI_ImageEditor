package com.example.mviimageeditor.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.lazy.LazyListState

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}

fun String.toAuthorizationCode(): String = this.substring(this.indexOf('=') + 1)

fun Bitmap.rotate(degrees: Float): Bitmap {
    if (degrees == 0f) return this

    val matrix =
        Matrix().apply {
            postRotate(degrees)
        }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
