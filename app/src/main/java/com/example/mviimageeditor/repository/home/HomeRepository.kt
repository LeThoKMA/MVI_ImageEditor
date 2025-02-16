package com.example.mviimageeditor.repository.home

import androidx.paging.PagingData
import com.example.mviimageeditor.model.CollectionModel
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getCollections(): Flow<PagingData<CollectionModel>>

    suspend fun likeImage(id: String): Flow<Unit>
}
