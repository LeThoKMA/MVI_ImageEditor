package com.example.mviimageeditor.module

import com.example.imageEditor2.model.CollectionModel
import com.example.imageEditor2.model.PhotoModel
import com.example.imageEditor2.model.PhotoSearchModel
import com.example.imageEditor2.utils.COLLECTION_ENDPOINT
import com.example.imageEditor2.utils.ID
import com.example.imageEditor2.utils.NAME
import com.example.imageEditor2.utils.PAGE
import com.example.imageEditor2.utils.PER_PAGE
import com.example.imageEditor2.utils.PHOTO_SEARCH_ENDPOINT
import com.example.imageEditor2.utils.QUERY_SEARCH
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET(COLLECTION_ENDPOINT)
    suspend fun getCollections(
        @Query(PAGE) page: Int,
    ): List<CollectionModel>

    @GET(PHOTO_SEARCH_ENDPOINT)
    suspend fun searchPhotos(
        @Query(PAGE) page: Int,
        @Query(QUERY_SEARCH) querySearch: String,
        @Query(PER_PAGE) perPage: Int = 10,
    ): PhotoSearchModel

    @POST("photos/{id}/like")
    suspend fun likeImage(
        @Path(ID) id: String,
    )

    @DELETE("photos/{id}/like")
    suspend fun dislikeImage(
        @Path(ID) id: String,
    )

    @GET("users/{name}/likes")
    suspend fun getFavoriteList(
        @Path(NAME) name: String,
        @Query(PAGE) page: Int,
    ): List<PhotoModel>
}
