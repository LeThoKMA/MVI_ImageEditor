package com.example.mviimageeditor.repository.authorize

import com.example.mviimageeditor.model.request.AuthorizeRequest
import com.example.mviimageeditor.model.response.AuthorizeResponse
import kotlinx.coroutines.flow.Flow

interface AuthorizeRepository {
    suspend fun authorize(authorizeRequest: AuthorizeRequest): Flow<AuthorizeResponse>
}
