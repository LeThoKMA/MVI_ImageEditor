package com.example.mviimageeditor.repository.home

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mviimageeditor.data.dao.CollectionDao
import com.example.mviimageeditor.module.Api
import com.example.mviimageeditor.paging3.CollectionRemoteMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(
    private val api: Api,
    private val dao: CollectionDao,
) : HomeRepository {
    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getCollections(): Flow<PagingData<com.example.mviimageeditor.model.CollectionModel>> =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(pageSize = 10),
                remoteMediator =
                    CollectionRemoteMediator(
                        apiService = api,
                        collectionDao = dao,
                    ),
                pagingSourceFactory = {
                    dao.getAll()
                },
            ).flow
        }

    override suspend fun likeImage(id: String): Flow<Unit> =
        withContext(Dispatchers.IO) {
            flow { emit(api.likeImage(id)) }
        }
}
