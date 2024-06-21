package com.example.imageEditor2.repository.authorize

import com.example.imageEditor2.ApiAuthorize
import com.example.imageEditor2.model.request.AuthorizeRequest
import com.example.imageEditor2.model.response.AuthorizeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class AuthorizeRepositoryImpl(private val apiAuthorize: ApiAuthorize) : AuthorizeRepository {
    override suspend fun authorize(authorizeRequest: AuthorizeRequest): Flow<AuthorizeResponse> =
        withContext(Dispatchers.IO) {
            flow {
                emit(apiAuthorize.authorize(authorizeRequest))
            }
        }
}
