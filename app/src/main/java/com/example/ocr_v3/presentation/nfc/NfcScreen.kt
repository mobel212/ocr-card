package com.example.ocr_v3.presentation.nfc

import android.app.Activity
import android.graphics.BitmapFactory
import android.nfc.NfcAdapter
import android.util.Log
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ocr_v3.R
import com.example.ocr_v3.presentation.components.InfoCard
import com.example.ocr_v3.presentation.components.ScanTypeTag
import com.example.ocr_v3.ui.theme.BackgroundLight
import com.example.ocr_v3.ui.theme.PrimaryPurple
import com.example.ocr_v3.ui.theme.Purple80
import com.example.ocr_v3.ui.theme.TextDark
import com.example.ocr_v3.ui.theme.TextGray
import java.io.File
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.Share

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcScreen(
    navController: NavController,
    viewModel: NfcTestViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK

        nfcAdapter?.enableReaderMode(context as? Activity, { tag ->
            viewModel.onTagDiscovered(tag)
        }, flags, null)

        onDispose {
            nfcAdapter?.disableReaderMode(context as? Activity)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NFC Reader", fontWeight = FontWeight.Bold) },
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
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header/Instructions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Moroccan CNIe",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = viewModel.statusMessage,
                        fontSize = 14.sp,
                        color = if (viewModel.statusMessage.startsWith("Error") || viewModel.statusMessage.startsWith(
                                "Failed"
                            )
                        ) Color.Red else TextGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (viewModel.scannedCard == null) {
                    // Instruction Image
                    Image(
                        painter = painterResource(id = R.drawable.nfchow), // Placeholder for NFC instruction
                        contentDescription = "How to scan",
                        modifier = Modifier
                            .fillMaxWidth()
//                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.FillWidth
                    )

                    Text(
                        "Tap and hold your card against the back of your phone",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                // CAN Input Dialog
                if (viewModel.showCanDialog) {
                    Dialog(onDismissRequest = { viewModel.onDismissCanDialog() }) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(28.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "Enter CAN Code",
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    fontSize = 20.sp
                                )
                                Text(
                                    "Found on the front of your card",
                                    color = TextGray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.cancodeplace),
                                    contentDescription = "where to find the CAN code",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.FillWidth
                                )

                                OutlinedTextField(
                                    value = viewModel.canCode,
                                    onValueChange = { viewModel.onCanCodeChanged(it) },
                                    label = { Text("6-Digit CAN") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    enabled = !viewModel.isLoading,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    TextButton(
                                        onClick = { viewModel.onDismissCanDialog() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Cancel", color = TextGray)
                                    }
                                    Button(
                                        onClick = { viewModel.onScanConfirmed() },
                                        enabled = viewModel.canCode.length == 6,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Start Scan", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                // Results
                viewModel.scannedCard?.let { card ->

                    val faceBitmap = viewModel.faceImageBitmap
                    if (faceBitmap != null && !faceBitmap.isRecycled) {
                        val imageBitmap = remember(faceBitmap) {
                            try {
                                faceBitmap.asImageBitmap()
                            } catch (e: Exception) {
                                Log.e("NfcScreen", "Failed to create ImageBitmap", e)
                                null
                            }
                        }
                        imageBitmap?.let {


                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    modifier = Modifier.size(150.dp),
                                    shadowElevation = 8.dp,
                                    color = Color.White
                                ) {
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = "face picture",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

//                                TextButton(
//                                    onClick = { viewModel.downloadFaceImage(context) },
//                                    modifier = Modifier.padding(top = 8.dp)
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Share,  // ← Material icon, always safe
//                                        contentDescription = null,
//                                        modifier = Modifier.size(18.dp)
//                                    )
//                                    Spacer(Modifier.width(8.dp))
//                                    Text("Save to Gallery", fontWeight = FontWeight.Bold)
//                                }

                                Button(
                                    onClick = {viewModel.downloadFaceImage(context)},
                                    modifier = Modifier.padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White,
                                        contentColor = PrimaryPurple),
                                ) {
                                    Text("save to Gallery", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Card Information",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            ScanTypeTag(scanType = card.scanType)
                        }

                        // Info Cards
                        InfoCard(label = "First Name", value = card.firstName, isReadOnly = true)
                        InfoCard(label = "Last Name", value = card.lastName, isReadOnly = true)
                        InfoCard(label = "ID Number", value = card.numId, isReadOnly = true)
                        InfoCard(label = "Birth Date", value = card.birthDate, isReadOnly = true)
                        InfoCard(
                            label = "Expiry Date",
                            value = card.expirationDate,
                            isReadOnly = true
                        )
                        InfoCard(
                            label = "Document Number",
                            value = card.documentNumber,
                            isReadOnly = true
                        )


                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.clearScan() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White,
                                contentColor = PrimaryPurple),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Scan Another Card", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            // Loading Overlay
            if (viewModel.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Reading Chip...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Keep your card steady",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
