package com.example.mviimageeditor.repository.search

import androidx.paging.PagingData
import com.example.mviimageeditor.model.PhotoModel
import com.example.mviimageeditor.model.PhotoSearchModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchPhotos(
        query: String,
    ): Flow<PagingData<PhotoModel>>
}
