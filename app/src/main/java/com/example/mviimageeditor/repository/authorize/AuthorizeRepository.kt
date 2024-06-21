package com.example.imageEditor2.repository.authorize

import com.example.imageEditor2.model.request.AuthorizeRequest
import com.example.imageEditor2.model.response.AuthorizeResponse
import kotlinx.coroutines.flow.Flow

interface AuthorizeRepository {
    suspend fun authorize(authorizeRequest: AuthorizeRequest): Flow<AuthorizeResponse>
}
