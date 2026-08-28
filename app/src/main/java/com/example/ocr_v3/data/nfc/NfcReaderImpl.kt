package com.example.ocr_v3.data.nfc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.nfc.tech.IsoDep
import android.util.Log
import com.gemalto.jp2.JP2Decoder
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.model.ScanType
import com.example.ocr_v3.domain.repository.NfcReader
import com.example.ocr_v3.domain.usecase.formatMrzDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import net.sf.scuba.smartcards.CardService
import org.jmrtd.PassportService
import org.jmrtd.PACEKeySpec
import org.jmrtd.lds.CardAccessFile
import org.jmrtd.lds.LDSFileUtil
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.DG1File
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class NfcReaderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NfcReader {

    override suspend fun readBiometricData(isoDep: IsoDep, can: String): Card {
        Log.i("NfcReader", "NFC Session Started")
        Log.i("NfcReader", "CAN: ${can.mask()}")

        var passportService: PassportService? = null
        try {
            if (!isoDep.isConnected) {
                isoDep.connect()
            }
            isoDep.timeout = 30000 // 30 seconds for biometric photo transfer

            val cardService = CardService.getInstance(isoDep)
            
            // Detect Extended Length support
            val maxTransceiveLength = isoDep.maxTransceiveLength
            val isExtendedLengthSupported = maxTransceiveLength > 256
            
            Log.d("NfcReader", "Max Transceive Length: $maxTransceiveLength, Extended Length Support: $isExtendedLengthSupported")

            passportService = PassportService(
                cardService,
                if (isExtendedLengthSupported) maxTransceiveLength else PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                if (isExtendedLengthSupported) maxTransceiveLength else PassportService.DEFAULT_MAX_BLOCKSIZE,
                isExtendedLengthSupported,
                false
            )

            passportService.open()
            Log.d("NfcReader", "[1/5] PassportService opened")

            // PACE with CAN
            val paceInfo = try {
                val stream = passportService.getInputStream(PassportService.EF_CARD_ACCESS)
                val file = CardAccessFile(stream)
                stream.close()
                file.securityInfos.firstOrNull { it is PACEInfo } as? PACEInfo
            } catch (e: Exception) {
                Log.w("NfcReader", "EF_CARD_ACCESS failed: ${e.message}")
                null
            }

            var paceSucceeded = false
            if (paceInfo != null) {
                try {
                    val paceKey = PACEKeySpec.createCANKey(can.trim())
                    passportService.doPACE(
                        paceKey,
                        paceInfo.objectIdentifier,
                        PACEInfo.toParameterSpec(paceInfo.parameterId),
                        paceInfo.parameterId
                    )
                    paceSucceeded = true
                    Log.i("NfcReader", "PACE Authentication Success")
                } catch (e: Exception) {
                    Log.e("NfcReader", "PACE Failed: ${e.message}")
                    throw e // CAN is required, no fallback
                }
            }

            passportService.sendSelectApplet(paceSucceeded)
            Log.d("NfcReader", "Applet selected")

            // Ensure connection before reading DG1
            ensureConnected(isoDep)

            // Read DG1 (essential data)
            Log.d("NfcReader", "Reading DG1...")
            val dg1Stream = passportService.getInputStream(PassportService.EF_DG1)
            val dg1File = LDSFileUtil.getLDSFile(PassportService.EF_DG1, dg1Stream) as DG1File
            dg1Stream.close()
            val mrz = dg1File.mrzInfo

            Log.i("NfcReader", "DG1 Data read successfully : ${mrz.toString()}")
            Log.i("NfcReader", "Doc Number : ${mrz.documentNumber}")
            Log.i("NfcReader", "Last Name  : ${mrz.primaryIdentifier?.replace("<", " ")}")
            Log.i("NfcReader", "First Name : ${mrz.secondaryIdentifier?.replace("<", " ")}")
            Log.i("NfcReader", "Birth Date : ${mrz.dateOfBirth}")
            Log.i("NfcReader", "Expiry     : ${mrz.dateOfExpiry}")

            // Ensure connection before reading DG2
            ensureConnected(isoDep)

            // Read DG2 and stream directly to file
            Log.d("NfcReader", "Reading DG2...")
            val photoPath = streamPhotoToFile(isoDep, passportService, mrz.documentNumber ?: "unknown")

            Log.i("NfcReader", "Session Complete")
            Log.i("NfcReader", "Photo: $photoPath")

            return Card(
                id = 0,
                firstName = mrz.secondaryIdentifier?.replace("<", " ")?.trim() ?: "",
                lastName = mrz.primaryIdentifier?.replace("<", " ")?.trim() ?: "",
                birthDate = formatMrzDate(mrz.dateOfBirth , true) ?: "",
                expirationDate = formatMrzDate(mrz.dateOfExpiry , false) ?: "",
                documentNumber = mrz.documentNumber,
                numId = mrz.personalNumber ?: "",
                address = "",
                faceImagePath = photoPath ,
                scanType = ScanType.NFC
            )

        } catch (e: Throwable) {
            Log.e("NfcReader", "FATAL ERROR: ${e.message}", e)
            val errorMessage = when (e) {
                is android.nfc.TagLostException -> "Tag lost. Keep the card steady."
                else -> e.message ?: "Unknown error"
            }
            return Card(
                id = 0,
                firstName = "ERROR",
                lastName = errorMessage,
                birthDate = "",
                expirationDate = "",
                documentNumber = "",
                numId = "",
                address = "" ,
                scanType = ScanType.OCR
            )
        } finally {
            try { 
                passportService?.close() 
            } catch (t: Throwable) {
                Log.e("NfcReader", "Error closing PassportService", t)
            }
            try { 
                if (isoDep.isConnected) isoDep.close() 
            } catch (t: Throwable) {
                Log.e("NfcReader", "Error closing IsoDep", t)
            }
            Log.d("NfcReader", "PassportService and IsoDep cleanup complete")
        }
    }

    private suspend fun streamPhotoToFile(isoDep: IsoDep, service: PassportService, docNumber: String): String? {
        return try {
            ensureConnected(isoDep)
            val stream = service.getInputStream(PassportService.EF_DG2)
            val dg2 = LDSFileUtil.getLDSFile(PassportService.EF_DG2, stream) as org.jmrtd.lds.icao.DG2File

            var savedPath: String? = null

            dg2.faceInfos.forEach { faceInfo ->
                faceInfo.faceImageInfos.forEach { imgInfo ->
                    val mimeType = imgInfo.mimeType
                    Log.i("NfcReader", "  Photo: MimeType=$mimeType, Size=${imgInfo.imageLength}")

                    val imageBytes = imgInfo.imageInputStream.readBytes()
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val isJp2 = mimeType.contains("jp2", ignoreCase = true) || mimeType.contains("jpx", ignoreCase = true)
                    
                    val dir = File(context.filesDir, "cnie_photos")
                    if (!dir.exists()) dir.mkdirs()

                    if (isJp2) {
                        try {
                            Log.i("NfcReader", "  Decoding JP2 image...")
                            val bitmap = JP2Decoder(imageBytes).decode()
                            val fileName = "cnie_${docNumber}_${timestamp}.jpg"
                            val file = File(dir, fileName)
                            
                            FileOutputStream(file).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                            }
                            
                            savedPath = file.absolutePath
                            Log.i("NfcReader", "  JP2 decoded and saved as JPEG: $savedPath")
                            bitmap.recycle()
                        } catch (e: Throwable) {
                            Log.e("NfcReader", "  Failed to decode JP2: ${e.message}", e)
                            // Fallback: save raw bytes as .jp2 so user can check externally
                            val fileName = "cnie_${docNumber}_${timestamp}.jp2"
                            val file = File(dir, fileName)
                            file.writeBytes(imageBytes)
                            savedPath = file.absolutePath
                            Log.w("NfcReader", "  Saved raw JP2 as fallback: $savedPath")
                        }
                    } else {
                        val fileName = "cnie_${docNumber}_${timestamp}.jpg"
                        val file = File(dir, fileName)
                        file.writeBytes(imageBytes)
                        compressIfOversized(file)
                        savedPath = file.absolutePath
                        Log.i("NfcReader", "  Photo saved to $savedPath")
                    }
                }
            }
            stream.close()
            savedPath
        } catch (e: Throwable) {
            Log.e("NfcReader", "DG2 stream failed: ${e.message}")
            null
        }
    }

    private suspend fun ensureConnected(isoDep: IsoDep) {
        var retries = 3
        while (!isoDep.isConnected && retries > 0) {
            try {
                isoDep.connect()
                Log.d("NfcReader", "Reconnected to IsoDep")
                break
            } catch (e: Exception) {
                retries--
                Log.e("NfcReader", "Failed to reconnect ($retries left): ${e.message}")
                if (retries > 0) delay(200)
            }
        }
    }

    private fun compressIfOversized(file: File, maxSizeMB: Int = 2) {
        val maxBytes = maxSizeMB * 1024 * 1024
        if (file.length() <= maxBytes) return

        Log.w("NfcReader", "  File too large (${file.length() / 1024 / 1024}MB), compressing...")

        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return
        val tempFile = File(file.parent, "${file.name}.tmp")

        FileOutputStream(tempFile).use { out ->
            val scaleFactor = if (file.length() > 10 * 1024 * 1024) 0.5f else 0.8f
            val scaled = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scaleFactor).toInt(),
                (bitmap.height * scaleFactor).toInt(),
                true
            )
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }

        bitmap.recycle()

        if (tempFile.length() < file.length()) {
            file.delete()
            tempFile.renameTo(file)
            Log.i("NfcReader", "  Compressed to ${file.length() / 1024}KB")
        } else {
            tempFile.delete()
        }
    }

    private fun String.mask(): String = if (length > 2) "${take(2)}****${takeLast(1)}" else "****"
}
