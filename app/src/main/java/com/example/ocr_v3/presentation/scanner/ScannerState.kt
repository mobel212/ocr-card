package com.example.ocr_v3.presentation.scanner

import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.model.ScanType


data class ScannerState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val scannedData: Card = Card(
        firstName = "",
        lastName = "",
        birthDate = "",
        expirationDate = "",
        documentNumber = "",
        numId = "",
        address = "",
        scanType = ScanType.OCR
    ),
    val error: Error? = null ,
    val statusMessage : String = "" ,
)
