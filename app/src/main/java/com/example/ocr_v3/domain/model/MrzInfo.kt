package com.example.ocr_v3.domain.model

data class MrzInfo(
    val documentNumber: String,
    val dateOfBirth: String, // Format: YYMMDD
    val dateOfExpiry: String // Format: YYMMDD
)