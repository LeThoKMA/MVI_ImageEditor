package com.example.imageEditor2.repository.search

import com.example.mviimageeditor.ui.theme.model.PhotoSearchModel
import com.example.mviimageeditor.module.Api
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
//
//    override suspend fun saveQueryToLocal(query: String): Flow<Unit> {
//        return withContext(Dispatchers.IO) {
//            flow { emit(historySearchDao.insert(QueryModel(content = query))) }
//        }
//    }
//
//    override suspend fun deleteQuery(queryModel: QueryModel): Flow<Unit> {
//        return withContext(Dispatchers.IO) {
//            flow { emit(historySearchDao.delete(queryModel)) }
//        }
//    }

//    override suspend fun getAllQuery(): Flow<List<QueryModel>> {
//        return withContext(Dispatchers.IO) {
//            flow { emit(historySearchDao.getAll()) }
//        }
//    }
}
