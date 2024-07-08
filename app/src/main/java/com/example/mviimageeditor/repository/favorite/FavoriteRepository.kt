package com.example.mviimageeditor.repository.favorite

import com.example.mviimageeditor.model.PhotoModel
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getFavoriteList(
        name: String,
        page: Int,
    ): Flow<List<PhotoModel>>

    suspend fun likeImage(id: String): Flow<Unit>

    suspend fun dislikeImage(id: String): Flow<Unit>
}
