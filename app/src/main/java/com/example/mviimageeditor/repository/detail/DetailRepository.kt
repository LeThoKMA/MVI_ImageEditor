package com.example.imageEditor2.repository.detail

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    suspend fun downloadImage(url: String): Flow<Unit>

    suspend fun saveImage(bitmap: Bitmap): Flow<Unit>
}
