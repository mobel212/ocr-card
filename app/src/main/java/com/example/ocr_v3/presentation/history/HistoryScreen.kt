package com.example.ocr_v3.presentation.history

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.ui.icons.AppIcons
import com.example.ocr_v3.ui.theme.BackgroundLight
import com.example.ocr_v3.ui.theme.PrimaryPurple
import com.example.ocr_v3.ui.theme.TextDark
import com.example.ocr_v3.ui.theme.TextGray
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.TextFieldDefaults

@Composable
fun HistoryScreen(navController: NavController, historyViewModel: HistoryViewModel = hiltViewModel()) {

    val cards by historyViewModel.cards.collectAsState(initial = emptyList())
    var selectedCard by remember { mutableStateOf<Card?>(null) }

    val searchText by historyViewModel.searchText.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // --- Header ---
        Text(
            text = "History",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
        )

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

@Composable
fun CardDetailDialog(card: Card, onDismiss: () -> Unit) {
    val clipboard = LocalClipboard.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Scanned Details",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextDark
            )
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("First Name", card.firstName, clipboard)
                DetailRow("Last Name", card.lastName, clipboard)
                DetailRow("ID Number", card.numId, clipboard)
                DetailRow("Birth Date", card.birthDate, clipboard)
                DetailRow("Expiration Date", card.expirationDate, clipboard)
                DetailRow("Address", card.address, clipboard)
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Close", color = PrimaryPurple, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String, clipboard: androidx.compose.ui.platform.Clipboard) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundLight.copy(alpha = 0.5f))
            .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 12.sp, color = TextGray)
            Text(
                text = if (value.isEmpty()) "---" else value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
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
            }
        ) {
            Icon(
                imageVector = AppIcons.ContentCopy,
                contentDescription = "Copy $label",
                tint = PrimaryPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accent line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${card.firstName} ${card.lastName}".uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1
                )
                Text(
                    text = "ID: ${card.numId}",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }

            IconButton(
                onClick = onDeleteClicked,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.05f))
            ) {
                Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
