package com.example.imageEditor2.repository.favorite

import com.example.mviimageeditor.ui.theme.model.PhotoModel
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getFavoriteList(
        name: String,
        page: Int,
    ): Flow<List<PhotoModel>>

    suspend fun likeImage(id: String): Flow<Unit>

    suspend fun dislikeImage(id: String): Flow<Unit>
}
