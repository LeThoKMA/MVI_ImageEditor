package com.example.mviimageeditor.main

import com.example.mviimageeditor.ContractViewModel

interface MainContract : ContractViewModel<MainContract.State, MainContract.Event, MainContract.Effect> {
    data class State(
        val isShowBottomBar: Boolean = false,
    )

    sealed class Event

    sealed class Effect
}
