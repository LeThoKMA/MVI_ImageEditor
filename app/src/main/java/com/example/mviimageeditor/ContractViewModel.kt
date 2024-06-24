package com.example.mviimageeditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ContractViewModel<STATE, EVENT, EFFECT> {
    val state: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
    fun event(event: EVENT)
}

@Composable
inline fun <reified STATE, EVENT, EFFECT> use(viewModel: ContractViewModel<STATE, EVENT, EFFECT>): StateDispatchEffect<STATE, EVENT, EFFECT> {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dispatch = { event: EVENT -> viewModel.event(event) }
    return StateDispatchEffect(state, dispatch, viewModel.effect)
}

data class StateDispatchEffect<STATE, EVENT, EFFECT>(
    val state: STATE,
    val dispatch: (EVENT) -> Unit,
    val effectFlow: SharedFlow<EFFECT>,
)