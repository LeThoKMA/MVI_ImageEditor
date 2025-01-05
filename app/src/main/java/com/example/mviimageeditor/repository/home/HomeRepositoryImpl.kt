package com.example.mviimageeditor.repository.home


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.imageEditor2.repository.home.HomeRepository
import com.example.mviimageeditor.module.Api
import com.example.mviimageeditor.paging3.CollectionPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(private val api: Api) : HomeRepository {
    override suspend fun getCollections(): Flow<PagingData<com.example.mviimageeditor.model.CollectionModel>> {
        return withContext(Dispatchers.IO) {
            Pager(PagingConfig(pageSize = 10)) {
                CollectionPagingSource(api)
            }.flow
        }
    }

    override suspend fun likeImage(id: String): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            flow { emit(api.likeImage(id)) }
        }
    }
}
