package com.example.mviimageeditor.ui.theme.home

import androidx.lifecycle.ViewModel
import com.example.imageEditor2.repository.home.HomeRepository
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.ContractViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel constructor(private val homeRepository: HomeRepository) : BaseViewModel(),
    ContractViewModel<HomeContract.State, HomeContract.Event, HomeContract.Effect> {
    private val _state = MutableStateFlow(HomeContract.State())
    override val state: StateFlow<HomeContract.State>
        get() = _state.asStateFlow()
    private val _effectFlow = MutableSharedFlow<HomeContract.Effect>()
    override val effect: SharedFlow<HomeContract.Effect>
        get() = _effectFlow.asSharedFlow()

    override fun event(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.OnClickImage -> {
                _state.update {
                    it.copy(
                        imageDetail = event.image
                    )
                }
            }

            else -> {

            }
        }
    }

}