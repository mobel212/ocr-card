package com.example.ocr_v3.presentation.scanner

import android.content.ClipData
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ocr_v3.presentation.Routes
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants
import java.nio.file.WatchEvent

@Composable
fun ResultScreen(
    viewModel: ScannerViewModel,
    navController: NavController
) {
    Log.e("er", "error 1 in result screen")

    val state = viewModel.theState
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    @Composable
    fun LabelUi(
        textValue: String,
        onValueChanged: (String) -> Unit,
        labelName: String
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textValue,
                onValueChange = onValueChanged,
                label = { Text(labelName) },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    scope.launch {
                        val clipData = ClipData.newPlainText("label", textValue)
                        clipboard.setClipEntry(ClipEntry(clipData))
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Copy")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Scan Result:",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LabelUi(
            state.scannedData.firstName,
            { newText ->
                viewModel.onFirstNameChanged(newText)
            },
            "FirstName"
        )

        LabelUi(
            state.scannedData.lastName,
            { newText ->
                viewModel.onLastNameChanged(newText)
            },
            "LastName"
        )

        LabelUi(
            state.scannedData.birthDate,
            { newText ->
                viewModel.onbirthDateChanged(newText)
            },
            "BirthDate"
        )

        LabelUi(
            state.scannedData.expirationDate,
            { newText ->
                viewModel.onExpDateChanged(newText)
            },
            "Expiration Date"
        )

        LabelUi(
            state.scannedData.numId,
            { newText ->
                viewModel.onNumIdChanged(newText)
            },
            "Id number"
        )

        LabelUi(
            state.scannedData.address,
            { newText ->
                viewModel.onAddressChanged(newText)
            },
            "Address"
        )

        Button(
            onClick = { viewModel.onInfosConfirmed() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Confirm infos of your card")
        }

        Button(
            onClick = { navController.navigate(Routes.CameraScreen) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color.Gray,       // Changes the background color
                contentColor = androidx.compose.ui.graphics.Color.White     // Changes the text/icon color
            )
        ) {
            Text("Retry")
        }
    }
}