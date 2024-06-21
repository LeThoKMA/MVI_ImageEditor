package com.example.mviimageeditor.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
data class BottomNavigationItem(
    val label: String,
    val icon: ImageVector,
    val route: Screen
)

fun getBottomNavigationItems(): List<BottomNavigationItem> = listOf(
    BottomNavigationItem(
        label = "Home",
        icon = Icons.Filled.Home,
        route = Screen.Home
    ),
    BottomNavigationItem(
        label = "Search",
        icon = Icons.Filled.Search,
        route = Screen.Search
    ),
    BottomNavigationItem(
        label = "Create",
        icon = Icons.Filled.Create,
        route = Screen.Create
    ),
    BottomNavigationItem(
        label = "Favorite",
        icon = Icons.Filled.FavoriteBorder,
        route = Screen.Favourites
    )
)