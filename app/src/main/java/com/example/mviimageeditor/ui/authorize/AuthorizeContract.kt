package com.example.mviimageeditor.ui.authorize

import com.example.mviimageeditor.ContractViewModel
import com.example.mviimageeditor.model.response.AuthorizeResponse

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