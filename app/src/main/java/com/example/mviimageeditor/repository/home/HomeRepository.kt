package com.example.imageEditor2.repository.home

import com.example.mviimageeditor.model.CollectionModel
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getCollections(page: Int): Flow<List<com.example.mviimageeditor.model.CollectionModel>>

    suspend fun likeImage(id: String): Flow<Unit>
}
