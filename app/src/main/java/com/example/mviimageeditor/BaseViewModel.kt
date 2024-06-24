package com.example.mviimageeditor

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

abstract class BaseViewModel() : ViewModel() {
    private val _baseSate = MutableStateFlow(State())
    val baseState: StateFlow<State>
        get() = _baseSate

    protected fun showLoading() {
        _baseSate.update {
            it.copy(
                isLoading = true
            )
        }
    }

    protected fun hideLoading() {
        _baseSate.update {
            it.copy(
                isLoading = false
            )
        }
    }

    protected fun handleApiError(error: Throwable?) {
        if (error == null) {
            _baseSate.update {
                it.copy(errorMessage = "Có lỗi xảy ra")
            }
            return
        }

        if (error is HttpException) {
            Log.w("ERROR", error.message() + error.code())
            when (error.code()) {
                HttpURLConnection.HTTP_BAD_REQUEST ->
                    try {
                        _baseSate.update {
                            it.copy(responseMessage = error.message())
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        _baseSate.update {
                            it.copy(responseMessage = error.message())
                        }
                    }

                HttpsURLConnection.HTTP_UNAUTHORIZED -> _baseSate.update {
                    it.copy(errorMessage = "Bạn không có quyền truy cập")
                }

                HttpsURLConnection.HTTP_FORBIDDEN, HttpsURLConnection.HTTP_INTERNAL_ERROR, HttpsURLConnection.HTTP_NOT_FOUND ->
                    _baseSate.update {
                        it.copy(responseMessage = error.message())
                    }

                else -> _baseSate.update {
                    it.copy(responseMessage = error.message())
                }
            }
        } else if (error is IOException) {
            Log.e("TAG", error.message.toString())
            _baseSate.update {
                it.copy(errorMessage = "Bạn không có quyền truy cập")
            }
        }
    }

    data class State(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseMessage: String? = null
    )
}
