package com.example.mviimageeditor.paging3

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.mviimageeditor.data.dao.CollectionDao
import com.example.mviimageeditor.model.CollectionModel
import com.example.mviimageeditor.module.Api

// class CollectionPagingSource(
//    private val apiService: Api,
//    private val collectionDao: CollectionDao,
// ) : PagingSource<Int, CollectionModel>() {
//    override fun getRefreshKey(state: PagingState<Int, CollectionModel>): Int? =
//        state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CollectionModel> {
//        try {
//            val page = params.key ?: 1
//            val response = apiService.getCollections(page)
//            collectionDao.insertAll(response.map { it.toCollectionEntity() })
//
//            return LoadResult.Page(
//                data = response,
//                prevKey = if (page == 1) null else page - 1,
//                nextKey = if (response.isEmpty()) null else page + 1,
//            )
//        } catch (e: Exception) {
//            val cachedData = collectionDao.getAll().load(params)
//            if (cachedData is LoadResult.Page) {
//                LoadResult.Page(
//                    data = cachedData.data.map { it.toCollectionModel() },
//                    prevKey = null,
//                    nextKey = null,
//                )
//            }
//            return LoadResult.Error(e)
//        }
//    }
// }

@OptIn(ExperimentalPagingApi::class)
class CollectionRemoteMediator(
    private val apiService: Api,
    private val collectionDao: CollectionDao,
) : RemoteMediator<Int, CollectionModel>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CollectionModel>,
    ): MediatorResult {
        return try {
            val page =
                when (loadType) {
                    LoadType.REFRESH -> 1 // Khi làm mới danh sách
                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) // Không tải thêm phía trên
                    LoadType.APPEND -> {
                        val lastItem = state.lastItemOrNull()
                        if (lastItem == null) {
                            return MediatorResult.Success(endOfPaginationReached = true)
                        } else {
                            state.pages.size + 1
                        }
                    }
                }

            val response = apiService.getCollections(page)

            collectionDao.insertAll(response)

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e) // Nếu API lỗi, tiếp tục lấy từ Room
        }
    }
}
