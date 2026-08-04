package com.example.ocr_v3.presentation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ocr_v3.presentation.camera.CameraScreen
import com.example.ocr_v3.presentation.history.HistoryScreen
import com.example.ocr_v3.presentation.history.HistoryViewModel
import com.example.ocr_v3.presentation.scanner.ResultScreen
import com.example.ocr_v3.presentation.scanner.ScannerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel


@Composable
fun AppNavigation() {
    val navControllerOfTheApp = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navControllerOfTheApp, startDestination = Routes.HomeScreen ){
        composable(Routes.HomeScreen){
            HomeScreen(context = context , navController = navControllerOfTheApp)
        }
        composable(Routes.CameraScreen){
            val viewModel: ScannerViewModel = hiltViewModel()
            CameraScreen(viewModel, navController = navControllerOfTheApp)
        }
        composable(Routes.HistoryScreen){
            val viewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(navControllerOfTheApp , viewModel)
        }

        composable(Routes.ResultScreen) {
            val cameraBackStackEntry = remember(it) {
                navControllerOfTheApp.getBackStackEntry(Routes.CameraScreen)
            }
            val viewModel: ScannerViewModel = hiltViewModel(cameraBackStackEntry)
            ResultScreen(viewModel, navControllerOfTheApp)
        }

    }
//    Scaffold(
//        modifier = Modifier.fillMaxSize() ,
//        bottomBar = {
//            NavigationBar() { }
//        }
//    ) {
//
//    }
}