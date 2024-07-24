package com.example.mviimageeditor.repository.home


import com.example.imageEditor2.repository.home.HomeRepository
import com.example.mviimageeditor.module.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(private val api: Api) : HomeRepository {
    override suspend fun getCollections(page: Int): Flow<List<com.example.mviimageeditor.model.CollectionModel>> {
        return withContext(Dispatchers.IO) {
            flow {
                emit(api.getCollections(page))
            }
        }
    }

    override suspend fun likeImage(id: String): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            flow { emit(api.likeImage(id)) }
        }
    }
}
