package com.example.ocr_v3.presentation.scanner

import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.model.MrzInfo


data class ScannerState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val scannedData: Card = Card(
        firstName = "",
        lastName = "",
        birthDate = "",
        expirationDate = "",
        numId = "",
        address = ""
    ),
    val error: Error? = null ,
    val statusMessage : String = "" ,
    val extractedMrzInfo: MrzInfo = MrzInfo(
        documentNumber = "",
        dateOfBirth = "" ,
        dateOfExpiry = ""
    )
)
