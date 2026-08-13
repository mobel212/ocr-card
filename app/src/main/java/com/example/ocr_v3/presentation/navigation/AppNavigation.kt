package com.example.ocr_v3.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ocr_v3.ui.theme.PrimaryPurple
import com.example.ocr_v3.ui.theme.TextGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ocr_v3.domain.repository.NfcReader
import com.example.ocr_v3.presentation.HomeScreen
import com.example.ocr_v3.presentation.camera.CameraScreen
import com.example.ocr_v3.presentation.camera.PhotoPreview
import com.example.ocr_v3.presentation.camera.SharedViewModel
import com.example.ocr_v3.presentation.history.HistoryScreen
import com.example.ocr_v3.presentation.history.HistoryViewModel
import com.example.ocr_v3.presentation.nfc.NfcScreen
import com.example.ocr_v3.presentation.nfc.NfcTestViewModel
import com.example.ocr_v3.presentation.scanner.ResultScreen
import com.example.ocr_v3.presentation.scanner.ScannerViewModel
import kotlinx.coroutines.launch


@Composable
fun AppNavigation() {
    val navControllerOfTheApp = rememberNavController()
    val context = LocalContext.current

    val navBackStackEntry by navControllerOfTheApp.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val sharedViewModel : SharedViewModel = hiltViewModel()
    val sviewModel : ScannerViewModel = hiltViewModel()

    val nfcViewModel : NfcTestViewModel = hiltViewModel()



    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) PrimaryPurple else TextGray
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                color = if (isSelected) PrimaryPurple else TextGray
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navControllerOfTheApp.navigate(item.route) {
                                popUpTo(navControllerOfTheApp.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
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
            composable(Routes.PhotoPreview) {
                val bitmap by sharedViewModel.photoBitmap.collectAsState()

                PhotoPreview(
                    onPhotoAccepted = { bitmap ->
                        scope.launch {
                            sviewModel.onPhotoAccepted(bitmap)
                            navControllerOfTheApp.navigate(Routes.ResultScreen)
                        }
                    },
                    navController = navControllerOfTheApp,
                    sharedViewModel = sharedViewModel,
                    scannerViewModel = sviewModel
                )
            }
            composable(Routes.CameraScreen) {
                CameraScreen(sharedViewModel, navController = navControllerOfTheApp)
            }
            composable(Routes.HistoryScreen) {
                val viewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(navControllerOfTheApp, viewModel)
            }
            composable(Routes.ResultScreen) {
                ResultScreen(sviewModel, navControllerOfTheApp)
            }

            composable(Routes.Nfc) {
                NfcScreen(nfcViewModel)
            }

        }
    }
}