package com.example.ocr_v3.presentation.scanner

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ocr_v3.presentation.navigation.Routes
import com.example.ocr_v3.ui.icons.AppIcons
import com.example.ocr_v3.ui.theme.BackgroundLight
import com.example.ocr_v3.ui.theme.PrimaryPurple
import com.example.ocr_v3.ui.theme.TextDark
import com.example.ocr_v3.ui.theme.TextGray
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    viewModel: ScannerViewModel,
    navController: NavController
) {
    val state = viewModel.theState
    val clipboard = LocalClipboard.current
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
            Text(
                text = "Scan Result",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Data Fields
            EditableInfoCard(
                label = "First Name",
                value = state.scannedData.firstName,
                onValueChange = { viewModel.onFirstNameChanged(it) },
                clipboard = clipboard
            )

            EditableInfoCard(
                label = "Last Name",
                value = state.scannedData.lastName,
                onValueChange = { viewModel.onLastNameChanged(it) },
                clipboard = clipboard
            )

            EditableInfoCard(
                label = "ID Number",
                value = state.scannedData.numId,
                onValueChange = { viewModel.onNumIdChanged(it) },
                clipboard = clipboard
            )

            EditableInfoCard(
                label = "Birth Date",
                value = state.scannedData.birthDate,
                onValueChange = { viewModel.onbirthDateChanged(it) },
                clipboard = clipboard
            )

            EditableInfoCard(
                label = "Expiration Date",
                value = state.scannedData.expirationDate,
                onValueChange = { viewModel.onExpDateChanged(it) },
                clipboard = clipboard
            )

            EditableInfoCard(
                label = "Address",
                value = state.scannedData.address,
                onValueChange = { viewModel.onAddressChanged(it) },
                clipboard = clipboard
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = { viewModel.onInfosConfirmed() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Confirm & Save", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { navController.navigate(Routes.CameraScreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryPurple
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Scan Again", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun EditableInfoCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    clipboard: androidx.compose.ui.platform.Clipboard
) {
    val scope = rememberCoroutineScope()
    
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = PrimaryPurple
                    ),
                    singleLine = true
                )
            }

            IconButton(
                onClick = {
                    if (value.isNotEmpty()) {
                        scope.launch {
                            val clipData = ClipData.newPlainText(label, value)
                            clipboard.setClipEntry(ClipEntry(clipData))
                        }
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = AppIcons.ContentCopy,
                    contentDescription = "Copy $label",
                    tint = PrimaryPurple.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
