package com.example.mviimageeditor.ui.search

import com.example.mviimageeditor.ContractViewModel
import com.example.mviimageeditor.model.PhotoModel
import com.example.mviimageeditor.utils.ALL

interface SearchContract :
    ContractViewModel<SearchContract.State, SearchContract.Event, SearchContract.Effect> {
    data class State(
        val images: List<PhotoModel>? = emptyList(),
        val page: Int = 1,
        val query: String = ALL
    )

    sealed class Event {
        data object OnLoadMore : Event()
        data class OnSearch(val query: String) : Event()

        data class OnViewDetail(val imageUrl: String) : Event()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()

        data class OnViewDetail(val imageUrl: String) : Effect()
    }

}