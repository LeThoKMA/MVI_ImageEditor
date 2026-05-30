package com.example.mviimageeditor.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.model.CollectionModel
import com.example.mviimageeditor.repository.home.HomeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel constructor(
    private val homeRepository: HomeRepository,
) : BaseViewModel<HomeContract.State, HomeContract.Event, HomeContract.Effect>() {
    override val _state: MutableStateFlow<HomeContract.State> =
        MutableStateFlow(HomeContract.State())
    override val _effect: MutableSharedFlow<HomeContract.Effect> = MutableSharedFlow()

    private val _pagingDataFlow: MutableStateFlow<PagingData<CollectionModel>> =
        MutableStateFlow(PagingData.empty())
    val pagingDataFlow: StateFlow<PagingData<CollectionModel>> = _pagingDataFlow

    init {
        fetchImage()
    }

    private fun fetchImage() {
        viewModelScope.launch {
            homeRepository
                .getCollections()
                .cachedIn(viewModelScope)
                .collect {
                    _pagingDataFlow.value = it
                }
        }
    }

    private fun likeImage(model: CollectionModel) {
        val updatedPagingData =
            _pagingDataFlow.value.map {
                if (it.id == model.id) {
                    it.copy(isLiked = !it.isLiked)
                } else {
                    it
                }
            }
        _pagingDataFlow.value = updatedPagingData
    }

    private fun navigateToDetail(imageUrl: String) {
        viewModelScope.launch {
            _effect.emit(HomeContract.Effect.OnViewDetail(imageUrl))
        }
    }

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.OnLoadMore -> {
                _state.update {
                    it.copy(
                        page = it.page + 1,
                    )
                }
            }

            is HomeContract.Event.OnLikeImage -> {
                likeImage(event.model)
            }

            is HomeContract.Event.OnViewDetail -> {
                navigateToDetail(event.imageUrl)
            }

            else -> {
            }
        }
    }
}
