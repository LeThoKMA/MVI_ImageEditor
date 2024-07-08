package com.example.mviimageeditor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

abstract class BaseViewModel() : ViewModel() {
    private val _baseSate = MutableStateFlow(State())
    private val _baseEffect = MutableSharedFlow<Effect>()
    val baseState: StateFlow<State>
        get() = _baseSate

    val baseEffect: SharedFlow<Effect>
        get() = _baseEffect

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
        viewModelScope.launch {
            if (error == null) {
                _baseSate.update {
                    it.copy(errorMessage = "Có lỗi xảy ra")
                }
                return@launch
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

                    HttpsURLConnection.HTTP_UNAUTHORIZED -> {
                        _baseSate.update {
                            it.copy(errorMessage = "Bạn không có quyền truy cập")
                        }
                        _baseEffect.emit(Effect.OnErrorAuthorize)

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
    }

    data class State(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseMessage: String? = null
    )

    sealed class Effect {
        data object OnErrorAuthorize : Effect()
    }
}
