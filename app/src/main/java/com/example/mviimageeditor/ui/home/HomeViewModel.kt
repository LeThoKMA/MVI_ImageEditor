package com.example.mviimageeditor.ui.home

import android.graphics.pdf.PdfDocument.Page
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageEditor2.repository.home.HomeRepository
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.ContractViewModel
import com.example.mviimageeditor.model.CollectionModel
import kotlinx.coroutines.delay
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

class HomeViewModel constructor(private val homeRepository: HomeRepository) : BaseViewModel(),
    ContractViewModel<HomeContract.State, HomeContract.Event, HomeContract.Effect> {
    private val _state = MutableStateFlow(HomeContract.State())
    override val state: StateFlow<HomeContract.State>
        get() = _state.asStateFlow()
    private val _effectFlow = MutableSharedFlow<HomeContract.Effect>()
    override val effect: SharedFlow<HomeContract.Effect>
        get() = _effectFlow.asSharedFlow()

    init {
        fetchImage()
    }

    private fun fetchImage() {
        viewModelScope.launch {
            homeRepository.getCollections(1)
                .onStart { showLoading() }
                .catch { handleApiError(it) }
                .onCompletion { hideLoading() }
                .collect { data ->
                    _state.update {
                        it.copy(images = data)
                    }
                }
        }
    }

    private fun loadMoreImage(page: Int) {
        viewModelScope.launch {
            homeRepository.getCollections(page)
                .onStart { showLoading() }
                .catch { handleApiError(it) }
                .onCompletion { hideLoading() }
                .collect { data ->
                    _state.update {
                        it.copy(images = it.images?.toMutableList().apply {
                            this?.addAll(data)
                        })
                    }
                }
        }
    }

    private fun likeImage(index: Int) {
        _state.update {
            it.copy(
                images = it.images.apply {
                    this?.get(index)?.isLiked = !this?.get(index)?.isLiked!!
                }
            )
        }
    }

    private fun navigateToDetail(imageUrl: String) {
        viewModelScope.launch {
            _effectFlow.emit(HomeContract.Effect.OnViewDetail(imageUrl))
        }
    }


    override fun event(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.OnLoadMore -> {
                _state.update {
                    it.copy(
                        page = it.page + 1
                    )
                }
                loadMoreImage(_state.value.page)
            }

            is HomeContract.Event.OnLikeImage -> {
                likeImage(event.index)
            }

            is HomeContract.Event.OnViewDetail -> {
                navigateToDetail(event.imageUrl)
            }

            else -> {

            }
        }
    }

}