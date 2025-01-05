package com.example.mviimageeditor.ui.home

import androidx.paging.PagingData
import com.example.mviimageeditor.ContractViewModel
import com.example.mviimageeditor.model.CollectionModel

interface HomeContract :
    ContractViewModel<HomeContract.State, HomeContract.Event, HomeContract.Effect> {
    data class State(
        val images: PagingData<com.example.mviimageeditor.model.CollectionModel> = PagingData.empty(),
        val page: Int = 1,
    )

    sealed class Event {
        data object OnLoadMore : Event()

        data class OnLikeImage(val model: CollectionModel) : Event()

        data class OnViewDetail(val imageUrl: String) : Event()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()

        data class OnViewDetail(val imageUrl: String) : Effect()
    }

}