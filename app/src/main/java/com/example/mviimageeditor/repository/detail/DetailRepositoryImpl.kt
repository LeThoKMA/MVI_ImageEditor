package com.example.mviimageeditor.repository.detail

import android.graphics.Bitmap
import com.example.mviimageeditor.download.DownloadService
import com.example.mviimageeditor.module.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DetailRepositoryImpl(
    private val downloadService: DownloadService,
    private val dispatcher: DispatcherProvider,
) : DetailRepository {
    override suspend fun downloadImage(url: String): Flow<Unit> =
        flow {
            emit(downloadService.downloadImage(url))
        }.flowOn(dispatcher.io())

    override suspend fun saveImage(bitmap: Bitmap): Flow<Unit> =
        flow { emit(downloadService.saveImage(bitmap)) }
            .flowOn(dispatcher.io())
}
