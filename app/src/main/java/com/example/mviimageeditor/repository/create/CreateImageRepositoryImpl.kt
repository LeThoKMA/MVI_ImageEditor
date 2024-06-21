package com.example.imageEditor2.repository.create

//import android.graphics.Bitmap
//import com.example.imageEditor2.download.DownloadService
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.withContext
//
//class CreateImageRepositoryImpl(private val downloadService: DownloadService) :
//    CreateImageRepository {
//    override suspend fun saveImage(bitmap: Bitmap): Flow<Unit> {
//        return withContext(Dispatchers.IO) {
//            flow { emit(downloadService.saveImage(bitmap)) }
//        }
//    }
//}
