package com.example.mviimageeditor

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mviimageeditor.nav.BottomNavigationItem
import com.example.mviimageeditor.nav.LocalAppNavigator
import com.example.mviimageeditor.nav.NavigatorImpl
import com.example.mviimageeditor.nav.Screen
import com.example.mviimageeditor.nav.appNavGraph
import com.example.mviimageeditor.nav.getBottomNavigationItems
import com.example.mviimageeditor.ui.theme.MVIImageEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        setContent {
            MVIImageEditorTheme {
                var navItemSelected by remember { mutableIntStateOf(0) }
                var isShowingBottomBar by remember { mutableStateOf(true) }
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
                        AnimatedVisibility(
                            visible = isShowingBottomBar,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(durationMillis = 100),
                            ), exit = slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(durationMillis = 100)
                            )
                        ) {
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
                        }
                    }) { innerPadding ->
                    CompositionLocalProvider(
                        LocalAppNavigator provides NavigatorImpl(navController)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            appNavGraph(innerPadding) {
                                isShowingBottomBar = it
                            }
                        }
                    }
                }
            }
        }
    }

    fun requestPermission() {
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false)) {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(intent)
                    }
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                ),
            )
        } else {
            launcher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
            )
        }
    }
}