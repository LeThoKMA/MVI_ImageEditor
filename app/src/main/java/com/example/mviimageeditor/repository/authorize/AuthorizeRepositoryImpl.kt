package com.example.mviimageeditor.repository.authorize

import com.example.mviimageeditor.ui.theme.model.request.AuthorizeRequest
import com.example.mviimageeditor.ui.theme.model.response.AuthorizeResponse
import com.example.mviimageeditor.module.ApiAuthorize
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
