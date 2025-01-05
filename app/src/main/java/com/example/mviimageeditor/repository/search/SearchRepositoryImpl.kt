package com.example.mviimageeditor.repository.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mviimageeditor.model.PhotoModel
import com.example.mviimageeditor.module.Api
import com.example.mviimageeditor.paging3.ImagePagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SearchRepositoryImpl(
    private val api: Api,
) :
    SearchRepository {
    override suspend fun searchPhotos(query: String): Flow<PagingData<PhotoModel>> {
        return withContext(Dispatchers.IO) {
            Pager(PagingConfig(pageSize = 15, prefetchDistance = 5)) {
                ImagePagingSource(api, query)
            }.flow
        }
    }
}
