package com.example.ocr_v3.presentation.history

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.presentation.CardEvent
import com.example.ocr_v3.presentation.scanner.ScannerViewModel
import com.example.ocr_v3.ui.theme.Purple40
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject


val cardBackgroundColor = Color(0xFFC1D4EB) // Light blue
val labelColor = Color(0xFF948EB5)          // Muted purple/gray
val valueColor = Color(0xFF673AB7)          // Darker purple
val dividerColor = Color(0xFF1C13A0)        // Deep blue line
@Composable
fun HistoryScreen (navController: NavController , historyViewModel: HistoryViewModel = hiltViewModel()){

    val cards by historyViewModel.cards.collectAsState(initial = emptyList())

    var selectedCard by remember { mutableStateOf<Card?>( null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp)

    ) {
        items(cards){
            card ->
            CardUi(card = card ,
                onCardClicked = {
                    selectedCard = card
                },
                onDeleteClicked ={ historyViewModel.deleteCard(card)}
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

    }
    if (selectedCard != null) {
        CardDetailDialog(
            card = selectedCard!!, // !! means we know it's not null here
            onDismiss = {
                selectedCard = null // Close the popup by resetting the state
            }
        )
    }
}


@Composable
fun CardDetailDialog(card: Card, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Card Details") },
        containerColor = cardBackgroundColor,
        text = {
            Column {
                Text("First Name: ${card.firstName}")
                Text("Last Name: ${card.lastName}")
                Text("ID Number: ${card.numId}")
                Text("Birth Date: ${card.birthDate}")
                Text("Address: ${card.address}")
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )
}

@Composable
fun CardUi(
    card : Card ,
    onCardClicked : ()-> Unit ,
    onDeleteClicked: () -> Unit
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable{onCardClicked()},
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Main Info Container (Takes up remaining space using weight)
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(cardBackgroundColor)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column {
                CardDataRow("FIRSTNAME", card.firstName, labelColor, valueColor)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 2.dp,
                    color = dividerColor
                )

                CardDataRow("LASTNAME", card.lastName, labelColor, valueColor)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 2.dp,
                    color = dividerColor
                )

                CardDataRow("ID NUMBER", card.numId, labelColor, valueColor)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. Action Buttons Column (Edit and Delete)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            IconButton(
                onClick = { onDeleteClicked() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "delete this card"
                )
            }
        }
    }

}

@Composable
fun CardDataRow(label: String, value: String, labelColor: Color, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = value.uppercase(), // Forces the extracted text to be uppercase like the image
            color = valueColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}