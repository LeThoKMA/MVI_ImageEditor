package com.example.imageEditor2.repository.home

import com.example.imageEditor2.model.CollectionModel
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getCollections(page: Int): Flow<List<CollectionModel>>

    suspend fun likeImage(id: String): Flow<Unit>
}
