package com.example.mviimageeditor

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mviimageeditor.nav.BottomNavigationItem
import com.example.mviimageeditor.nav.Screen
import com.example.mviimageeditor.nav.getBottomNavigationItems
import com.example.mviimageeditor.ui.theme.Favorite.FavoriteScreen
import com.example.mviimageeditor.ui.theme.MVIImageEditorTheme
import com.example.mviimageeditor.ui.theme.create.CreateScreen
import com.example.mviimageeditor.ui.theme.home.HomeScreen
import com.example.mviimageeditor.ui.theme.search.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVIImageEditorTheme {
                var navItemSelected by remember { mutableIntStateOf(0) }
                val viewModel = viewModel<MainViewModel>()
                val navController = rememberNavController()
                val navSelectedCallBack = remember<(Int, BottomNavigationItem) -> Unit> {
                    { index, bottomNavItem ->
                        navItemSelected = index
                        navController.navigate(bottomNavItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar() {
                            getBottomNavigationItems().forEachIndexed { index, bottomNavItem ->
                                NavigationBarItem(
                                    selected = index == navItemSelected,
                                    label = { Text(bottomNavItem.label) },
                                    icon = {
                                        Icon(bottomNavItem.icon, bottomNavItem.label)
                                    },
                                    onClick = {
                                        navSelectedCallBack(index, bottomNavItem)
                                    }
                                )
                            }

                        }
                    }) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Screen.Home> {
                            HomeScreen()
                        }
                        composable<Screen.Search> {
                            SearchScreen()
                        }
                        composable<Screen.Create> {
                            CreateScreen()
                        }
                        composable<Screen.Favourites> {
                            FavoriteScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MVIImageEditorTheme {
        Greeting("Android")
    }
}