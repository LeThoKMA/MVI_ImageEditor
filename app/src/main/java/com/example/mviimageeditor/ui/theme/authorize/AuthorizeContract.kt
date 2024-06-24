package com.example.mviimageeditor.ui.theme.authorize

import com.example.mviimageeditor.ui.theme.model.response.AuthorizeResponse
import com.example.mviimageeditor.ContractViewModel

interface AuthorizeContract :
    ContractViewModel<AuthorizeContract.State, AuthorizeContract.Event, AuthorizeContract.Effect> {
    data class State(
        val authorizeResponse: AuthorizeResponse? = null
    )

    sealed class Event {
        data class OnAuthorize(val authorizationCode: String) : Event()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }
}