package com.example.mviimageeditor.module

import com.example.mviimageeditor.ui.theme.model.request.AuthorizeRequest
import com.example.mviimageeditor.ui.theme.model.response.AuthorizeResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiAuthorize {
    @POST("oauth/token")
    suspend fun authorize(
        @Body request: AuthorizeRequest,
    ): AuthorizeResponse
}
