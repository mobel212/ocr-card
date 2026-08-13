package com.example.ocr_v3.presentation.nfc

import android.app.Activity
import android.nfc.NfcAdapter
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NfcScreen(viewModel: NfcTestViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity

    DisposableEffect(Unit) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK

        nfcAdapter?.enableReaderMode(activity, { tag ->
            viewModel.onTagDiscovered(tag)
        }, flags, null)

        onDispose {
            nfcAdapter?.disableReaderMode(activity)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NFC Test Mode",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hardcoded keys are active.\nTap your card now.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = viewModel.statusMessage,
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}