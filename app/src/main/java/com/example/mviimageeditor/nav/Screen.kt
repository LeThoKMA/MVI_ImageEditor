package com.example.mviimageeditor.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mviimageeditor.model.CollectionModel
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Create : Screen

    @Serializable
    data object Favourites : Screen

    @Serializable
    data class Details(val image: String? = null) : Screen

}