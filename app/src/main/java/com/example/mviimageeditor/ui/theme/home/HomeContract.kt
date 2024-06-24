package com.example.mviimageeditor.ui.theme.home

import com.example.mviimageeditor.ui.theme.model.CollectionModel
import com.example.mviimageeditor.ContractViewModel

interface HomeContract :
    ContractViewModel<HomeContract.State, HomeContract.Event, HomeContract.Effect> {
    data class State(
        val images: List<CollectionModel>? = emptyList(),
        val imageDetail: CollectionModel? = null
    )

    sealed class Event {
        data class OnClickImage(val image: CollectionModel) : Event()
        data class OnLoadMore(val page: Int) : Event()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }

}