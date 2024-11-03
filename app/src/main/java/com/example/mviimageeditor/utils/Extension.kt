package com.example.mviimageeditor.utils

import androidx.compose.foundation.lazy.LazyListState

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}

fun String.toAuthorizationCode(): String {
    return this.substring(this.indexOf('=') + 1)
}
