package com.example.imageEditor2.repository.search

import com.example.imageEditor2.model.PhotoSearchModel
import com.example.imageEditor2.model.QueryModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchPhotos(
        page: Int,
        query: String,
    ): Flow<PhotoSearchModel>

//    suspend fun saveQueryToLocal(query: String): Flow<Unit>
//
//    suspend fun deleteQuery(queryModel: QueryModel): Flow<Unit>
//
//    suspend fun getAllQuery(): Flow<List<QueryModel>>
}
