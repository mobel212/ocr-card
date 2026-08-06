package com.example.ocr_v3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ocr_v3.presentation.navigation.AppNavigation
import com.example.ocr_v3.ui.theme.Ocrv3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ocrv3Theme {
                AppNavigation()
                    }
            }
        }
    }


