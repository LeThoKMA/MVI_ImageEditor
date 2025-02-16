package com.example.mviimageeditor.repository.authorize

import com.example.mviimageeditor.model.request.AuthorizeRequest
import com.example.mviimageeditor.model.response.AuthorizeResponse
import com.example.mviimageeditor.module.ApiAuthorize
import com.example.mviimageeditor.module.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthorizeRepositoryImpl(
    private val apiAuthorize: ApiAuthorize,
    private val dispatcher: DispatcherProvider,
) : AuthorizeRepository {
    override suspend fun authorize(authorizeRequest: AuthorizeRequest): Flow<AuthorizeResponse> =
        flow {
            emit(apiAuthorize.authorize(authorizeRequest))
        }.flowOn(dispatcher.io())
}
