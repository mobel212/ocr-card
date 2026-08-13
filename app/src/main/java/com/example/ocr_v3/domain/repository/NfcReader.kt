package com.example.ocr_v3.domain.repository

import android.nfc.tech.IsoDep
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.model.MrzInfo

interface NfcReader {
    // We pass the raw NFC tag connection (IsoDep) and the MRZ password to unlock it
    suspend fun readBiometricData(isoDep: IsoDep , can : String? , mrzInfo: MrzInfo?): Card
}