package com.example.mviimageeditor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@Suppress("ktlint:standard:backing-property-naming")
abstract class BaseViewModel<STATE, EVENT, EFFECT> : ViewModel() {
    protected abstract val _state: MutableStateFlow<STATE>
    protected abstract val _effect: MutableSharedFlow<EFFECT>

    val state: StateFlow<STATE>
        get() = _state.asStateFlow()

    val effect: SharedFlow<EFFECT>
        get() = _effect.asSharedFlow()

    abstract fun handleEvent(event: EVENT)

    protected fun handleApiError(error: Throwable?) {}

//    protected fun handleApiError(error: Throwable?) {
//        if (error == null) {
//            Error.UnknownError
//            return
//        }
//
//        if (error is HttpException) {
//            Log.w("ERROR", error.message() + error.code())
//            when (error.code()) {
//                HttpURLConnection.HTTP_BAD_REQUEST ->
//                    Error.NetworkError(error.message(), error.code())
//
//                HttpsURLConnection.HTTP_UNAUTHORIZED -> {
//                    Error.NetworkError(error.message(), error.code())
//                }
//
//                HttpsURLConnection.HTTP_FORBIDDEN, HttpsURLConnection.HTTP_INTERNAL_ERROR, HttpsURLConnection.HTTP_NOT_FOUND -> {
//                    Error.NetworkError(error.message(), error.code())
//                }
//
//                else -> {
//                    Error.NetworkError(error.message(), error.code())
//                }
//            }
//        } else if (error is IOException) {
//            Log.e("TAG", error.message.toString())
//            Error.IOError(error.message.toString())
//        }
//    }
//
//    sealed class Error(
//        message: String,
//    ) {
//        data object UnknownError : Error(message = "Có lỗi xảy ra")
//
//        data class NetworkError(
//            val message: String,
//            val type: Int,
//        ) : Error(message = message)
//
//        data class IOError(
//            val message: String,
//        ) : Error(message = message)
//    }
}
