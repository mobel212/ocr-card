package com.example.ocr_v3.presentation.history

import android.app.Activity
import android.content.ClipData
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.model.ScanType
import com.example.ocr_v3.presentation.components.InfoCard
import com.example.ocr_v3.presentation.components.ScanTypeTag
import com.example.ocr_v3.ui.icons.AppIcons
import com.example.ocr_v3.ui.theme.BackgroundLight
import com.example.ocr_v3.ui.theme.PrimaryPurple
import com.example.ocr_v3.ui.theme.TextDark
import com.example.ocr_v3.ui.theme.TextGray
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, historyViewModel: HistoryViewModel = hiltViewModel()) {

    val cards by historyViewModel.cards.collectAsState(initial = emptyList())
    var selectedCard by remember { mutableStateOf<Card?>(null) }

    val searchText by historyViewModel.searchText.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding)
                .background(BackgroundLight)
        ) {
//            // --- Header ---
//            Text(
//                text = "History",
//                fontSize = 28.sp,
//                fontWeight = FontWeight.Bold,
//                color = TextDark,
//                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
//            )

            //search bar
            TextField(
                value = searchText,
                onValueChange = historyViewModel::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                placeholder = { Text(text = "Search...", color = TextGray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextGray
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = { historyViewModel.onSearchTextChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = TextGray
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (cards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No recent scans found", color = TextGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cards) { card ->
                        CardUi(
                            card = card,
                            onCardClicked = { selectedCard = card },
                            onDeleteClicked = { historyViewModel.deleteCard(card) }
                        )
                    }
                }
            }
        }

        if (selectedCard != null) {
            CardDetailDialog(
                card = selectedCard!!,
                onDismiss = { selectedCard = null }
            )
        }
    }
}

@Composable
fun CardDetailDialog(card: Card, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = BackgroundLight,
        shape = RoundedCornerShape(28.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Photo Header
                card.faceImagePath?.let { path ->
                    val bitmap = remember(path) {
                        BitmapFactory.decodeFile(path)?.asImageBitmap()
                    }
                    bitmap?.let {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 4.dp
                        ) {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Card Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextDark
                    )
                    ScanTypeTag(scanType = card.scanType)
                }

                InfoCard(label = "First Name", value = card.firstName, isReadOnly = true)
                InfoCard(label = "Last Name", value = card.lastName, isReadOnly = true)
                InfoCard(label = "ID Number", value = card.numId, isReadOnly = true)
                InfoCard(label = "Birth Date", value = card.birthDate, isReadOnly = true)
                InfoCard(label = "Expiration Date", value = card.expirationDate, isReadOnly = true)
                
                if (card.scanType == ScanType.OCR) {
                    InfoCard(label = "Address", value = card.address ?: "---", isReadOnly = true)
                }
                
                InfoCard(label = "Document Number", value = card.documentNumber, isReadOnly = true)
            }
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun CardUi(
    card: Card,
    onCardClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClicked() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Face Image Thumbnail or ScanType Indicator
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundLight),
                contentAlignment = Alignment.Center
            ) {
                val bitmap = remember(card.faceImagePath) {
                    card.faceImagePath?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
                }
                
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = if (card.scanType == ScanType.NFC) AppIcons.Nfc else AppIcons.PhotoCamera,
                        contentDescription = null,
                        tint = PrimaryPurple.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${card.firstName} ${card.lastName}".uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    ScanTypeTag(scanType = card.scanType)
                }
                Text(
                    text = "ID: ${card.numId}",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            IconButton(
                onClick = onDeleteClicked,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.05f))
            ) {
                Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
