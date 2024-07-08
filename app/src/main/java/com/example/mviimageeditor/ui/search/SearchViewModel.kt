package com.example.mviimageeditor.ui.search

import androidx.lifecycle.viewModelScope
import com.example.mviimageeditor.repository.search.SearchRepository
import com.example.mviimageeditor.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(private val searchRepository: SearchRepository) : BaseViewModel(),
    SearchContract {
    private val _state = MutableStateFlow(SearchContract.State())
    private val _effect = MutableSharedFlow<SearchContract.Effect>()
    override val state: StateFlow<SearchContract.State>
        get() = _state
    override val effect: SharedFlow<SearchContract.Effect>
        get() = _effect

    override fun event(event: SearchContract.Event) {
        when (event) {
            is SearchContract.Event.OnLoadMore -> {
                onLoadMore()
            }

            is SearchContract.Event.OnSearch -> {
                searchPhotos(event.query)
            }

            is SearchContract.Event.OnViewDetail -> {
                navigateToDetail(event.imageUrl)
            }

            else -> {}
        }
    }

    init {
        fetchDataDefault()
    }

    private fun onLoadMore() {
        viewModelScope.launch {
            _state.update { it.copy(page = it.page + 1) }
            searchRepository.searchPhotos(_state.value.page, _state.value.query)
                .catch { handleApiError(it) }
                .collect { data ->
                    _state.update {
                        it.copy(images = it.images?.toMutableList().apply {
                            this?.addAll(data.photoModels)
                        })
                    }
                }
        }
    }

    private fun fetchDataDefault() {
        viewModelScope.launch {
            searchRepository.searchPhotos(_state.value.page, _state.value.query)
                .catch { handleApiError(it) }
                .collect { data ->
                    _state.update {
                        it.copy(images = data.photoModels)
                    }
                }
        }
    }

    private fun searchPhotos(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(query = query, page = 1) }
            searchRepository.searchPhotos(_state.value.page, _state.value.query)
                .onStart { showLoading() }
                .onCompletion { hideLoading() }
                .catch { handleApiError(it) }
                .collect { data ->
                    _state.update {
                        it.copy(images = data.photoModels)
                    }
                }
        }
    }

    private fun navigateToDetail(url: String) {
        viewModelScope.launch {
            _effect.emit(SearchContract.Effect.OnViewDetail(url))
        }
    }
}