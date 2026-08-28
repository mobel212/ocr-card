package com.example.ocr_v3.presentation.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.ocr_v3.domain.model.ScanType
import com.example.ocr_v3.presentation.components.InfoCard
import com.example.ocr_v3.presentation.components.ScanTypeTag
import com.example.ocr_v3.presentation.navigation.Routes
import com.example.ocr_v3.ui.theme.BackgroundLight
import com.example.ocr_v3.ui.theme.PrimaryPurple
import com.example.ocr_v3.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: ScannerViewModel,
    navController: NavController
) {
    val state = viewModel.theState
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Handle success message
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            snackbarHostState.showSnackbar("Card saved successfully!")
            viewModel.resetSavedStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Result", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextDark
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Summary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                ScanTypeTag(scanType = state.scannedData.scanType)
            }

            // Data Fields
            InfoCard(
                label = "First Name",
                value = state.scannedData.firstName,
                onValueChange = { viewModel.onFirstNameChanged(it) }
            )

            InfoCard(
                label = "Last Name",
                value = state.scannedData.lastName,
                onValueChange = { viewModel.onLastNameChanged(it) }
            )

            InfoCard(
                label = "ID Number",
                value = state.scannedData.numId,
                onValueChange = { viewModel.onNumIdChanged(it) }
            )

            InfoCard(
                label = "Birth Date",
                value = state.scannedData.birthDate,
                onValueChange = { viewModel.onbirthDateChanged(it) }
            )

            InfoCard(
                label = "Expiration Date",
                value = state.scannedData.expirationDate,
                onValueChange = { viewModel.onExpDateChanged(it) }
            )
            if (state.scannedData.scanType == ScanType.OCR) {
                InfoCard(
                    label = "Address",
                    value = state.scannedData.address ?: " ",
                    onValueChange = { viewModel.onAddressChanged(it) }
                )
            }

            InfoCard(
                label = "Document number",
                value = state.scannedData.documentNumber,
                onValueChange = { viewModel.onDocNumChanged(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = { viewModel.onInfosConfirmed() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Confirm & Save", fontSize = 18.sp, fontWeight = FontWeight.SemiBold , color = BackgroundLight)
            }

            Button(
                onClick = {
                    navController.navigate(Routes.CameraScreen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryPurple
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Scan Again", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
