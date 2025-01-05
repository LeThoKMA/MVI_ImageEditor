package com.example.mviimageeditor.ui.search

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.repository.search.SearchRepository
import com.example.mviimageeditor.utils.ALL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(private val searchRepository: SearchRepository) :
    BaseViewModel(),
    SearchContract {
    private val _state = MutableStateFlow(SearchContract.State())
    private val _effect = MutableSharedFlow<SearchContract.Effect>()
    override val state: StateFlow<SearchContract.State>
        get() = _state
    override val effect: SharedFlow<SearchContract.Effect>
        get() = _effect

    private val querySearchState = MutableStateFlow(ALL)
    val pagingDataFlow =
        querySearchState
            .flatMapLatest { searchRepository.searchPhotos(it) }
            .cachedIn(viewModelScope) // Sử dụng cachedIn để lưu trữ dữ liệu phân trang.

    override fun event(event: SearchContract.Event) {
        viewModelScope.launch {
            when (event) {
                is SearchContract.Event.OnLoadMore -> {
                }

                is SearchContract.Event.OnSearch -> {
                    searchData(event.query)
                }

                is SearchContract.Event.OnViewDetail -> {
                    navigateToDetail(event.imageUrl)
                }

                else -> {}
            }
        }
    }

    private suspend fun searchData(query: String) {
        querySearchState.emit(query)
    }

    private fun navigateToDetail(url: String?) {
        viewModelScope.launch {
            url?.let {
                _effect.emit(SearchContract.Effect.OnViewDetail(it))
            }
        }
    }
}
