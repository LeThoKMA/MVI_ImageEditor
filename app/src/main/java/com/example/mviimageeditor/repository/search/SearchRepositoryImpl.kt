package com.example.mviimageeditor.repository.search

import com.example.mviimageeditor.model.PhotoSearchModel
import com.example.mviimageeditor.module.Api
import com.example.mviimageeditor.repository.search.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SearchRepositoryImpl(
    private val api: Api,
) :
    SearchRepository {
    override suspend fun searchPhotos(
        page: Int,
        query: String,
    ): Flow<PhotoSearchModel> {
        return withContext(Dispatchers.IO) {
            flow { emit(api.searchPhotos(page, query)) }
        }
    }
}
