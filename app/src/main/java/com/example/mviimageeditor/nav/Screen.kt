package com.example.mviimageeditor.nav

import kotlinx.serialization.Serializable

sealed interface Screen {
    sealed class BottomNav : Screen {
        @Serializable
        data object Home : Screen

        @Serializable
        data object Search : Screen

        @Serializable
        data object Create : Screen

        @Serializable
        data object Favourites : Screen
    }

    @Serializable
    data class Details(
        val image: String? = null,
    ) : Screen
}
