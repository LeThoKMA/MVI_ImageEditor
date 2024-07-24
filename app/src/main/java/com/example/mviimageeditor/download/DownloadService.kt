package com.example.mviimageeditor.download

import android.graphics.Bitmap

interface DownloadService {
    suspend fun downloadImage(url: String)

    suspend fun saveImage(bitmap: Bitmap)
}
