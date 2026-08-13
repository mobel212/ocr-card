package com.example.ocr_v3.presentation.nfc

import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocr_v3.domain.repository.NfcReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NfcTestViewModel @Inject constructor(
    private val nfcReader: NfcReader
) : ViewModel() {

    var statusMessage by mutableStateOf("Tap your card to the phone to test.")
        private set

    fun onTagDiscovered(tag: Tag) {
        val isoDep = IsoDep.get(tag)
        if (isoDep == null) {
            statusMessage = "❌ Not an ISO-DEP card."
            return
        }

        statusMessage = "📡 Reading... Keep the card steady."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Pass nulls — the reader uses its own hardcoded test values
                val result = nfcReader.readBiometricData(isoDep, null, null)

                withContext(Dispatchers.Main) {
                    statusMessage = if (result.firstName == "ERROR") {
                        "❌ Read failed: ${result.lastName}"
                    } else {
                        "✅ ${result.firstName} ${result.lastName}\n" +
                                "🆔 ${result.numId}\n" +
                                "🎂 ${result.birthDate} | ⏳ ${result.expirationDate} ${result.address}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    statusMessage = "❌ Exception: ${e.message}"
                }
            }
        }
    }
}