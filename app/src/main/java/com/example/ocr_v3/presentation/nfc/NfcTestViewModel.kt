package com.example.ocr_v3.presentation.nfc

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocr_v3.domain.model.ScanType
import com.example.ocr_v3.domain.repository.CardRepository
import com.example.ocr_v3.domain.repository.NfcReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class NfcTestViewModel @Inject constructor(
    private val nfcReader: NfcReader,
    private val cardRepository: CardRepository
) : ViewModel() {

    var canCode by mutableStateOf("")
        private set

    var statusMessage by mutableStateOf("Enter your 6-digit CAN code, then tap the card.")
        private set

    var scannedCard by mutableStateOf<com.example.ocr_v3.domain.model.Card?>(null)
        private set

    var faceImageBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var showCanDialog by mutableStateOf(false)
        private set

    private var pendingTag: android.nfc.Tag? = null

    // -----------------------------------------------------------
    // UI INPUT
    // -----------------------------------------------------------

    fun onCanCodeChanged(newCode: String) {
        if (newCode.all { it.isDigit() } && newCode.length <= 6) {
            canCode = newCode
            Log.d("NfcViewModel", "CAN input updated: ${newCode.mask()}")
        }
    }

    fun onDismissCanDialog() {
        showCanDialog = false
        pendingTag = null
    }

    fun onScanConfirmed() {
        val tag = pendingTag ?: return
        if (canCode.length == 6) {
            showCanDialog = false
            executeScan(tag)
        }
    }

    fun clearScan() {
        canCode = ""
        scannedCard = null
        faceImageBitmap = null
        statusMessage = "Enter your 6-digit CAN code, then tap the card."
        Log.d("NfcViewModel", "Scan cleared")
    }

    // -----------------------------------------------------------
    // NFC DISCOVERY
    // -----------------------------------------------------------

    fun onTagDiscovered(tag: Tag) {
        pendingTag = tag
        showCanDialog = true
    }

    private fun executeScan(tag: Tag) {
        val isoDep = IsoDep.get(tag)
        if (isoDep == null) {
            statusMessage = "Not a smart card."
            Log.w("NfcViewModel", "Tag is not ISO-DEP. Tech: ${tag.techList.joinToString()}")
            return
        }

        isLoading = true
        statusMessage = "Reading... Keep card steady."
        Log.i("NfcViewModel", "=== READ START | CAN: ${canCode.mask()} ===")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val card = nfcReader.readBiometricData(isoDep, canCode)

                if (card.firstName == "ERROR") {
                    withContext(Dispatchers.Main) {
                        isLoading = false
                        statusMessage = "Failed: ${card.lastName}"
                        scannedCard = null
                        faceImageBitmap = null
                        Log.e("NfcViewModel", "Read failed: ${card.lastName}")
                    }
                } else {


                    val bitmap = card.faceImagePath?.let { path ->
                        try {
                            // Decode bounds first to check size
                            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                            BitmapFactory.decodeFile(path, options)

                            // Calculate sample size to fit ~1000px max dimension
                            val maxDim = 1000
                            var sampleSize = 1
                            while (options.outWidth / sampleSize > maxDim || options.outHeight / sampleSize > maxDim) {
                                sampleSize *= 2
                            }

                            // Decode scaled bitmap
                            BitmapFactory.Options().apply { inSampleSize = sampleSize }
                                .let { BitmapFactory.decodeFile(path, it) }
                        } catch (e: Exception) {
                            Log.e("NfcViewModel", "Bitmap decoding failed", e)
                            null
                        }
                    }

                    withContext(Dispatchers.Main) {
                        try {
                            isLoading = false
                            faceImageBitmap = bitmap
                            scannedCard = card
                            statusMessage = "${card.firstName} ${card.lastName} read successfully!"
                            Log.i("NfcViewModel", "Read OK | ID=${card.numId} | Photo=${card.faceImagePath}")

                            cardRepository.insertCard(card)
                        } catch (t: Throwable) {
                            Log.e("NfcViewModel", "CRASH IN POST-READ: ${t.javaClass.name}: ${t.message}", t)
                            statusMessage = "Display/DB error: ${t.message}"
                        }
                    }
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    statusMessage = "Error: ${t.message}"
                    scannedCard = null
                    Log.e("NfcViewModel", "NFC Flow Error", t)
                }
            }
        }
    }

    fun downloadFaceImage(context: Context) {
        val card = scannedCard ?: return
        val imagePath = card.faceImagePath ?: return
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeFile(imagePath) ?: return@launch
                val fileName = "CNIe_${card.numId}_${System.currentTimeMillis()}.jpg"
                
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                }
                
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    }
                    withContext(Dispatchers.Main) {
                        statusMessage = "Image saved to Gallery!"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    statusMessage = "Failed to save image: ${e.message}"
                }
            }
        }
    }

    fun saveCard() {
        viewModelScope.launch {
            scannedCard?.let { card ->
                cardRepository.insertCard(card)
            }
        }
    }
    private fun String.mask(): String = if (length > 2) "${take(2)}****${takeLast(1)}" else "****"
}
