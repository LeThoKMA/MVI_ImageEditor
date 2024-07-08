package com.example.mviimageeditor.repository.search

import com.example.mviimageeditor.model.PhotoSearchModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchPhotos(
        page: Int,
        query: String,
    ): Flow<PhotoSearchModel>
}
