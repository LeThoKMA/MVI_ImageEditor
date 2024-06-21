package com.example.imageEditor2.repository.create

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface CreateImageRepository {
    suspend fun saveImage(bitmap: Bitmap): Flow<Unit>
}
