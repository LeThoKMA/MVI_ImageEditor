package com.example.mviimageeditor.paging3

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mviimageeditor.model.CollectionModel
import com.example.mviimageeditor.model.PhotoModel
import com.example.mviimageeditor.module.Api

class ImagePagingSource(private val apiService: Api, private val query: String) :
    PagingSource<Int, PhotoModel>() {
    override fun getRefreshKey(state: PagingState<Int, PhotoModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoModel> {
        return try {
            val page = params.key ?: 1
            val response = apiService.searchPhotos(page,query)
            LoadResult.Page(
                data = response.photoModels,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.photoModels.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}