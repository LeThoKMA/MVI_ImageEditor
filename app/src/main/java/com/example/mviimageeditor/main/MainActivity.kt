package com.example.mviimageeditor.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mviimageeditor.nav.BottomNavigationItem
import com.example.mviimageeditor.nav.LocalAppNavigator
import com.example.mviimageeditor.nav.NavigatorImpl
import com.example.mviimageeditor.nav.Screen
import com.example.mviimageeditor.nav.appNavGraph
import com.example.mviimageeditor.nav.bottomNavGraph
import com.example.mviimageeditor.nav.getBottomNavigationItems
import com.example.mviimageeditor.permission.PermissionManager
import com.example.mviimageeditor.ui.authorize.ui.theme.MVIImageEditorTheme
import com.google.ar.core.ArCoreApk
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : AppCompatActivity() {
    @SuppressLint("RestrictedApi")
    @OptIn(ExperimentalSerializationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ArCoreApk
                .getInstance()
                .checkAvailability(this) != ArCoreApk.Availability.SUPPORTED_INSTALLED
        ) {
            Toast.makeText(this, "ARCore not supported on this device", Toast.LENGTH_LONG).show()
        }
        val permissionManager =
            PermissionManager(this) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!it.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false)) {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(intent)
                    }
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager.requestPermission(listOf(Manifest.permission.POST_NOTIFICATIONS))
        }
        setContent {
            MVIImageEditorTheme {
                val scope = rememberCoroutineScope()
                var navItemSelected by remember { mutableIntStateOf(0) }
                val navController = rememberNavController()
                val navigator =
                    remember {
                        NavigatorImpl(navController, scope)
                    }
                val isShowBottomBar by navigator.isShowBottomBar.collectAsStateWithLifecycle()

                val navSelectedCallBack =
                    remember<(Int, BottomNavigationItem) -> Unit> {
                        { index, bottomNavItem ->
                            navItemSelected = index
                            navigator.navigate(bottomNavItem.route)
                        }
                    }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = isShowBottomBar,
                            enter =
                                slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(durationMillis = 100),
                                ),
                            exit =
                                slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(durationMillis = 100),
                                ),
                        ) {
                            NavigationBar {
                                getBottomNavigationItems().forEachIndexed { index, bottomNavItem ->
                                    NavigationBarItem(
                                        selected = index == navItemSelected,
                                        label = { Text(bottomNavItem.label) },
                                        icon = {
                                            Icon(bottomNavItem.icon, bottomNavItem.label)
                                        },
                                        onClick = {
                                            navSelectedCallBack(index, bottomNavItem)
                                        },
                                    )
                                }
                            }
                        }
                    },
                ) { innerPadding ->
                    CompositionLocalProvider(
                        LocalAppNavigator provides navigator,
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.BottomNav.Home,
                            modifier =
                                Modifier
                                    .fillMaxSize(),
                        ) {
                            bottomNavGraph(innerPadding)
                            appNavGraph(innerPadding)
                        }
                    }
                }
            }
        }
    }
}
