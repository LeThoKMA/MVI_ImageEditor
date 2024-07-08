package com.example.mviimageeditor.ui.home

import com.example.mviimageeditor.model.CollectionModel
import com.example.mviimageeditor.ContractViewModel

interface HomeContract :
    ContractViewModel<HomeContract.State, HomeContract.Event, HomeContract.Effect> {
    data class State(
        val images: List<com.example.mviimageeditor.model.CollectionModel>? = emptyList(),
        val page: Int = 1
    )

    sealed class Event {
        data object OnLoadMore : Event()

        data class OnLikeImage(val index: Int): Event()

        data class OnViewDetail(val imageUrl: String) : Event()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()

        data class OnViewDetail(val imageUrl: String) : Effect()
    }

}