package com.example.imageEditor2.repository.home


import com.example.mviimageeditor.ui.theme.model.CollectionModel
import com.example.mviimageeditor.module.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(private val api: Api) : HomeRepository {
    override suspend fun getCollections(page: Int): Flow<List<CollectionModel>> {
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
