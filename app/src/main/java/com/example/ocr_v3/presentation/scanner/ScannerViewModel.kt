package com.example.ocr_v3.presentation.scanner

import android.graphics.Bitmap
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocr_v3.data.mlkit.TextRecognizer
import com.example.ocr_v3.domain.repository.CardRepository
import com.example.ocr_v3.domain.repository.NfcReader
import com.example.ocr_v3.domain.usecase.ParseCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val textRecognizer: TextRecognizer,
    private val parseCardUseCase: ParseCardUseCase,
    private val cardRepositoryImpl: CardRepository,
    private val nfcReader: NfcReader
) : ViewModel() {

    var theState by mutableStateOf(ScannerState())
        private set

    fun onFirstNameChanged(newOne : String){
        val currentCard = theState.scannedData
        val newCard = currentCard.copy(firstName = newOne)
        theState = theState.copy(scannedData = newCard)
    }
    fun onLastNameChanged(newOne : String){
        val currentCard = theState.scannedData
        val newCard = currentCard.copy(lastName = newOne)
        theState = theState.copy(scannedData = newCard)
    }
    fun onbirthDateChanged(newOne : String){
        val currentCard = theState.scannedData
        val newCard = currentCard.copy(birthDate = newOne)
        theState = theState.copy(scannedData = newCard)
    }
    fun onExpDateChanged(newOne : String){
        val currentCard = theState.scannedData
        val newCard = currentCard.copy(expirationDate = newOne)
        theState = theState.copy(scannedData = newCard)
    }
    fun onNumIdChanged(newOne : String){
        val currentCard = theState.scannedData
        val newCard = currentCard.copy(numId = newOne)
        theState = theState.copy(scannedData = newCard)
    }
    fun onAddressChanged(newOne : String){
        val currentCard = theState.scannedData
        val newCard = currentCard.copy(address = newOne)
        theState = theState.copy(scannedData = newCard)
    }

    suspend fun onPhotoAccepted(photo: Bitmap) {
        theState = theState.copy(isLoading = true)
        suspendCancellableCoroutine { continuation ->
            textRecognizer.textRecognizer(
                bitmap = photo,
                onResult = { text ->
                    Log.e("text extracted", "here the text extracted : $text")
                    val card = parseCardUseCase.invoke(text)
                    val mrzInfo = parseCardUseCase.extractMrzOnly(text)
                    theState = theState.copy(scannedData = card, extractedMrzInfo = mrzInfo, isLoading = false)
                    Log.e("text parsed", "here the text extracted : ${card.toString()}")
                    if (continuation.isActive) continuation.resume(Unit)
                },
                onError = {
                    Log.e("viewmodel error ", "error in view model in text recognizer")
                    theState = theState.copy(isLoading = false)
                    if (continuation.isActive) continuation.resume(Unit)
                }
            )
        }
    }

    fun onInfosConfirmed() {
        viewModelScope.launch {
            cardRepositoryImpl.insertCard(theState.scannedData)
            theState = theState.copy(isSaved = true)
        }
    }

    fun resetSavedStatus() {
        theState = theState.copy(isSaved = false)
    }

    //for nfc
    fun onNfcTagDiscovered(tag: android.nfc.Tag) {
        val isoDep = IsoDep.get(tag)
        if (isoDep != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    theState = theState.copy(isLoading = true, statusMessage = "Authenticating chip...")

                    // Assuming you already extracted MRZ info via ML Kit earlier
                    val currentMrz = theState.extractedMrzInfo

                    val nfcCard = nfcReader.readBiometricData(isoDep, null, currentMrz)
                    theState = theState.copy(scannedData = nfcCard, isLoading = false)

                } catch (e: Exception) {
                    theState = theState.copy(error = e.message as Error?, isLoading = false)
                }
            }
        }
    }
}
