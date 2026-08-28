package com.example.ocr_v3.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ocr_v3.ui.icons.AppIcons

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(title = "Home", route = Routes.HomeScreen, icon = Icons.Default.Home),
    BottomNavItem(title = "Scan", route = Routes.CameraScreen, icon = AppIcons.PhotoCamera),
    BottomNavItem(title = "History", route = Routes.HistoryScreen, icon = AppIcons.List),
    BottomNavItem(title = "Nfc" , route = Routes.Nfc , icon = AppIcons.Nfc )
)

