package com.example.mviimageeditor.repository.detail

import android.graphics.Bitmap
import com.example.mviimageeditor.download.DownloadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class DetailRepositoryImpl(private val downloadService: DownloadService) : DetailRepository {
    override suspend fun downloadImage(url: String): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            flow {
                emit(downloadService.downloadImage(url))
            }
        }
    }

    override suspend fun saveImage(bitmap: Bitmap): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            flow { emit(downloadService.saveImage(bitmap)) }
        }
    }
}
