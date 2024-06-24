package com.example.mviimageeditor.ui.theme.home

import android.graphics.pdf.PdfDocument.Page
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageEditor2.repository.home.HomeRepository
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.ContractViewModel
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
            homeRepository.getCollections(0)
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
                        it.copy(images = data)
                    }
                }
        }
    }

    override fun event(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.OnClickImage -> {
                _state.update {
                    it.copy(
                        imageDetail = event.image
                    )
                }
            }

            is HomeContract.Event.OnLoadMore -> {
                loadMoreImage(event.page)
            }

            else -> {

            }
        }
    }

}