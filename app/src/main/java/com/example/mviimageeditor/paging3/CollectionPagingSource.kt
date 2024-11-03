package com.example.mviimageeditor.paging3

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mviimageeditor.model.CollectionModel
import com.example.mviimageeditor.module.Api

class CollectionPagingSource(private val apiService: Api) : PagingSource<Int, CollectionModel>() {
    override fun getRefreshKey(state: PagingState<Int, CollectionModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CollectionModel> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getCollections(page)
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}