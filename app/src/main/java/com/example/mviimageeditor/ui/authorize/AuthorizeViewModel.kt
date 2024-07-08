package com.example.mviimageeditor.ui.authorize

import androidx.lifecycle.viewModelScope
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.ContractViewModel
import com.example.mviimageeditor.MyPreference
import com.example.mviimageeditor.model.request.AuthorizeRequest
import com.example.mviimageeditor.module.NetworkModule
import com.example.mviimageeditor.repository.authorize.AuthorizeRepository
import com.example.mviimageeditor.utils.ACCESS_KEY
import com.example.mviimageeditor.utils.REDIRECT_URI
import com.example.mviimageeditor.utils.SECRET_KEY
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthorizeViewModel(
    private val authorizeRepository: AuthorizeRepository,
    private val myPreference: MyPreference
) : BaseViewModel(),
    ContractViewModel<AuthorizeContract.State, AuthorizeContract.Event, AuthorizeContract.Effect> {
    private val _state = MutableStateFlow(AuthorizeContract.State())

    private val _effectFlow = MutableSharedFlow<AuthorizeContract.Effect>()

    private fun authorize(authorizationCode: String) {
        viewModelScope.launch {
            val authorizeRequest =
                AuthorizeRequest(
                    clientId = ACCESS_KEY,
                    clientSecret = SECRET_KEY,
                    redirectUri = REDIRECT_URI,
                    code = authorizationCode,
                )
            authorizeRepository.authorize(authorizeRequest).onStart { showLoading() }
                .catch { handleApiError(it) }
                .onCompletion { hideLoading() }
                .collect { data ->
                    myPreference.saveToken(data.accessToken)
                    _state.update {
                        it.copy(
                            authorizeResponse = data
                        )
                    }
                }
        }
    }

    override fun event(event: AuthorizeContract.Event) {
        when (event) {
            is AuthorizeContract.Event.OnAuthorize -> authorize(event.authorizationCode)
        }
    }

    override val state: StateFlow<AuthorizeContract.State>
        get() = _state.asStateFlow()
    override val effect: SharedFlow<AuthorizeContract.Effect>
        get() = _effectFlow.asSharedFlow()
}
