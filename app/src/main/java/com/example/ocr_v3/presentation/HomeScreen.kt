package com.example.ocr_v3.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun HomeScreen(
    context: Context = LocalContext.current,
    navController: NavController = rememberNavController()
) {
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    if(!hasCameraPermission){
        Text("Camera permission is required.")
    }else {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(20.dp)
                .size(30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text="welcome to card scanner",
                fontSize = 24.sp
                )

            Button(
                onClick = { navController.navigate(Routes.CameraScreen) },
                Modifier.padding(20.dp)
                    .size(200.dp ,60.dp)
            ) {
                Text(text ="scan your card" ,
                    fontSize = 19.sp)
            }

            Button(
                onClick = { navController.navigate(Routes.HistoryScreen)} ,
                    Modifier.padding(20.dp)
                    .size(200.dp ,60.dp)
            ) {
                Text(text ="History" ,
                    fontSize = 19.sp)
            }
        }
    }
}