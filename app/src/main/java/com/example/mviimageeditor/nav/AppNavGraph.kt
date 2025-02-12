package com.example.mviimageeditor.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mviimageeditor.ui.Favorite.FavoriteScreen
import com.example.mviimageeditor.ui.create.CreateScreen
import com.example.mviimageeditor.ui.detail.DetailScreen
import com.example.mviimageeditor.ui.home.HomeScreen
import com.example.mviimageeditor.ui.search.SearchScreen

fun NavGraphBuilder.appNavGraph(innerPaddingValues: PaddingValues) {
    composable<Screen.Details> {
        DetailScreen()
    }
}

fun NavGraphBuilder.bottomNavGraph(innerPaddingValues: PaddingValues) {
    composable<Screen.BottomNav.Home> {
        HomeScreen(innerPaddingValues)
    }
    composable<Screen.BottomNav.Search> {
        SearchScreen(innerPaddingValues)
    }
    composable<Screen.BottomNav.Create> {
        CreateScreen()
    }
    composable<Screen.BottomNav.Favourites> {
        FavoriteScreen()
    }
}
