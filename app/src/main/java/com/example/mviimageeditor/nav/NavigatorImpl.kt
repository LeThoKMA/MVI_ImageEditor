package com.example.mviimageeditor.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

class NavigatorImpl(private val navController: NavController) : Navigator {
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