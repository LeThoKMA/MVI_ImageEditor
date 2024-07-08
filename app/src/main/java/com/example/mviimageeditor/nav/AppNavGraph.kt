package com.example.mviimageeditor.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mviimageeditor.ui.detail.DetailScreen
import com.example.mviimageeditor.ui.Favorite.FavoriteScreen
import com.example.mviimageeditor.ui.create.CreateScreen
import com.example.mviimageeditor.ui.home.HomeScreen
import com.example.mviimageeditor.ui.search.SearchScreen


fun NavGraphBuilder.appNavGraph(
    innerPaddingValues: PaddingValues,
    onShowingBottomBar: (Boolean) -> Unit
) {
    composable<Screen.Home> {
        onShowingBottomBar.invoke(true)
        HomeScreen(innerPaddingValues)
    }
    composable<Screen.Search> {
        onShowingBottomBar.invoke(true)
        SearchScreen(innerPaddingValues)
    }
    composable<Screen.Create> {
        CreateScreen()
    }
    composable<Screen.Favourites> {
        FavoriteScreen()
    }
    composable<Screen.Details>(
//        enterTransition = {
//            fadeIn(
//                animationSpec = tween(
//                    500, easing = LinearEasing
//                )
//            ) + slideInVertically(
//                animationSpec = tween(500, easing = EaseIn),
//            )
//        },
//        exitTransition = {
//            fadeOut(
//                animationSpec = tween(
//                    500, easing = LinearEasing
//                )
//            ) + slideOutVertically(
//                animationSpec = tween(500, easing = EaseOut),
//            )
//        }
    ) {
        onShowingBottomBar.invoke(false)
        it.arguments?.getString("image")
            ?.let { it1 -> DetailScreen(it1) }
    }
}