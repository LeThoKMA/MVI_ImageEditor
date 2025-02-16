package com.example.mviimageeditor.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class NavigatorImpl(
    private val navController: NavController,
    scope: CoroutineScope,
) : Navigator {
    private val _isShowBottomBar = MutableStateFlow(true)
    val isShowBottomBar = _isShowBottomBar.asStateFlow()

    init {
        scope.launch {
            navController.currentBackStackEntryFlow.collectLatest {
                when (it.destination.route) {
                    Screen.BottomNav.Home
                        .serializer()
                        .descriptor.serialName,
                    Screen.BottomNav.Search
                        .serializer()
                        .descriptor.serialName,
                    Screen.BottomNav.Create
                        .serializer()
                        .descriptor.serialName,
                    Screen.BottomNav.Favourites
                        .serializer()
                        .descriptor.serialName,
                    -> _isShowBottomBar.update { true }

                    else -> _isShowBottomBar.update { false }
                }
            }
        }
    }

    override fun navigate(screen: Screen) {
        navController.navigate(screen) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun navigateBack() {
        navController.popBackStack()
    }

    override fun navigateToChild(screen: Screen) {
        navController.navigate(screen) {
            launchSingleTop = true
            restoreState = true
        }
    }
}
