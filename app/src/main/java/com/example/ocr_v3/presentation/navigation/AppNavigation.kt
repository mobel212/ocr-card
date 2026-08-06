package com.example.ocr_v3.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ocr_v3.presentation.HomeScreen
import com.example.ocr_v3.presentation.camera.CameraScreen
import com.example.ocr_v3.presentation.history.HistoryScreen
import com.example.ocr_v3.presentation.history.HistoryViewModel
import com.example.ocr_v3.presentation.scanner.ResultScreen
import com.example.ocr_v3.presentation.scanner.ScannerViewModel


@Composable
fun AppNavigation() {
    val navControllerOfTheApp = rememberNavController()
    val context = LocalContext.current

    val navBackStackEntry by navControllerOfTheApp.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
                NavigationBar {
                    // 3. This loop actually draws the buttons!
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                            label = { Text(text = item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navControllerOfTheApp.navigate(item.route) {
                                    // 4. THESE THREE LINES PREVENT THE APP FROM GETTING SLOW
                                    popUpTo(navControllerOfTheApp.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navControllerOfTheApp,
            startDestination = Routes.HomeScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HomeScreen) {
                HomeScreen(context = context, navController = navControllerOfTheApp)
            }
            composable(Routes.CameraScreen) {
                val viewModel: ScannerViewModel = hiltViewModel()
                CameraScreen(viewModel, navController = navControllerOfTheApp)
            }
            composable(Routes.HistoryScreen) {
                val viewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(navControllerOfTheApp, viewModel)
            }
            composable(Routes.ResultScreen) {
                val cameraBackStackEntry = remember(it) {
                    navControllerOfTheApp.getBackStackEntry(Routes.CameraScreen)
                }
                val viewModel: ScannerViewModel = hiltViewModel(cameraBackStackEntry)
                ResultScreen(viewModel, navControllerOfTheApp)
            }

        }
    }
}