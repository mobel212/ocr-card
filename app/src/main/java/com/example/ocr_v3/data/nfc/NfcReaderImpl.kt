package com.example.ocr_v3.data.nfc

import android.nfc.tech.IsoDep
import android.util.Base64
import android.util.Log
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.model.MrzInfo
import com.example.ocr_v3.domain.repository.NfcReader
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import org.jmrtd.PassportService
import org.jmrtd.PACEKeySpec
import org.jmrtd.lds.CardAccessFile
import org.jmrtd.lds.LDSFileUtil
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG11File
import org.jmrtd.lds.icao.DG12File
import org.jmrtd.lds.icao.DG14File
import org.jmrtd.lds.icao.DG15File
import javax.inject.Inject

class NfcReaderImpl @Inject constructor() : NfcReader {

    private val TEST_CAN = "131883"
    private val TEST_MRZ = MrzInfo(
        documentNumber = "EA1234567",
        dateOfBirth = "950415",
        dateOfExpiry = "300414"
    )

    override suspend fun readBiometricData(isoDep: IsoDep, can: String?, mrzInfo: MrzInfo?): Card {
        isoDep.timeout = 15000
        val cardService = CardService.getInstance(isoDep)
        val passportService = PassportService(cardService,
            PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
            PassportService.DEFAULT_MAX_BLOCKSIZE,
            false,
            false)

        try {
            passportService.open()
            Log.i("NfcReader", "╔══════════════════════════════════════════════════════╗")
            Log.i("NfcReader", "║         MOROCCAN CNIE NFC READ SESSION START         ║")
            Log.i("NfcReader", "╚══════════════════════════════════════════════════════╝")

            // ── PACE ──
            val paceInfo = try {
                val cardAccessStream = passportService.getInputStream(PassportService.EF_CARD_ACCESS)
                val cardAccessFile = CardAccessFile(cardAccessStream)
                cardAccessStream.close()
                cardAccessFile.securityInfos.firstOrNull { it is PACEInfo } as? PACEInfo
            } catch (e: Exception) {
                Log.w("NfcReader", "EF_CARD_ACCESS failed: ${e.message}")
                null
            }

            var paceSucceeded = false
            if (paceInfo != null) {
                try {
                    val paceKey = PACEKeySpec.createCANKey(TEST_CAN)
                    passportService.doPACE(
                        paceKey,
                        paceInfo.objectIdentifier,
                        PACEInfo.toParameterSpec(paceInfo.parameterId),
                        paceInfo.parameterId
                    )
                    paceSucceeded = true
                    Log.i("NfcReader", "✅ PACE SUCCESS")
                } catch (e: Exception) {
                    Log.e("NfcReader", "❌ PACE FAILED: ${e.message}")
                }
            }

            passportService.sendSelectApplet(paceSucceeded)

            // BAC fallback
            if (!paceSucceeded) {
                try {
                    passportService.getInputStream(PassportService.EF_COM).read()
                } catch (e: Exception) {
                    val bacKey = BACKey(TEST_MRZ.documentNumber, TEST_MRZ.dateOfBirth, TEST_MRZ.dateOfExpiry)
                    passportService.doBAC(bacKey)
                    Log.i("NfcReader", "✅ BAC SUCCESS")
                }
            }

            // ═══════════════════════════════════════════════════════
            // READ ALL DATA GROUPS — each wrapped in try/catch
            // ═══════════════════════════════════════════════════════

            // DG1
            val dg1File = readDG1(passportService)

            // DG2
            val faceImageBytes = readDG2(passportService)

            // DG11
            readDG11(passportService)

            // DG12
            readDG12(passportService)

            // DG14
            readDG14(passportService)

            // DG15
            readDG15(passportService)

            // SOD
            readSOD(passportService)

            Log.i("NfcReader", "╔══════════════════════════════════════════════════════╗")
            Log.i("NfcReader", "║              ALL DATA GROUPS READ COMPLETE           ║")
            Log.i("NfcReader", "╚══════════════════════════════════════════════════════╝")

            return Card(
                id = 0,
                firstName = dg1File?.secondaryIdentifier?.replace("<", " ")?.trim() ?: "",
                lastName = dg1File?.primaryIdentifier?.replace("<", " ")?.trim() ?: "",
                birthDate = dg1File?.dateOfBirth ?: "",
                expirationDate = dg1File?.dateOfExpiry ?: "",
                numId = dg1File?.documentNumber ?: "",
                address = faceImageBytes?.size?.let { "Photo: $it bytes" } ?: ""
            )

        } catch (e: Exception) {
            Log.e("NfcReader", "❌ FATAL ERROR: ${e.message}", e)
            return Card(
                id = 0,
                firstName = "ERROR",
                lastName = e.message ?: "Unknown",
                birthDate = "",
                expirationDate = "",
                numId = "",
                address = ""
            )
        } finally {
            try { passportService.close() } catch (_: Exception) {}
            Log.i("NfcReader", "=== NFC SESSION CLOSED ===")
        }
    }

    // ═══════════════════════════════════════════════════════
    // DG1: MRZ DATA
    // ═══════════════════════════════════════════════════════
    private fun readDG1(service: PassportService): org.jmrtd.lds.icao.MRZInfo? {
        return try {
            val stream = service.getInputStream(PassportService.EF_DG1)
            val dg1 = LDSFileUtil.getLDSFile(PassportService.EF_DG1, stream) as DG1File
            stream.close()

            val mrz = dg1.mrzInfo

            Log.i("NfcReader", "┌─────────────────────────────────────┐")
            Log.i("NfcReader", "│  DG1 - MRZ DATA                     │")
            Log.i("NfcReader", "├─────────────────────────────────────┤")
            Log.i("NfcReader", "│ Document Number : ${mrz.documentNumber}")
            Log.i("NfcReader", "│ Last Name       : ${mrz.primaryIdentifier?.replace("<", " ")}")
            Log.i("NfcReader", "│ First Name      : ${mrz.secondaryIdentifier?.replace("<", " ")}")
            Log.i("NfcReader", "│ Date of Birth   : ${mrz.dateOfBirth}")
            Log.i("NfcReader", "│ Gender          : ${mrz.gender}")
            Log.i("NfcReader", "│ Nationality     : ${mrz.nationality}")
            Log.i("NfcReader", "│ Date of Expiry  : ${mrz.dateOfExpiry}")
            Log.i("NfcReader", "│ Issuing State   : ${mrz.issuingState}")
            Log.i("NfcReader", "│ Optional Data 1 : ${mrz.optionalData1}")
            Log.i("NfcReader", "│ Optional Data 2 : ${mrz.optionalData2 ?: "N/A"}")
            Log.i("NfcReader", "│ Personal Number : ${mrz.personalNumber ?: "N/A"}")
            Log.i("NfcReader", "└─────────────────────────────────────┘")

            mrz
        } catch (e: Exception) {
            Log.e("NfcReader", "❌ DG1 failed: ${e.message}")
            null
        }
    }

    // ═══════════════════════════════════════════════════════
    // DG2: PORTRAIT PHOTO
    // ═══════════════════════════════════════════════════════
    private fun readDG2(service: PassportService): ByteArray? {
        return try {
            val stream = service.getInputStream(PassportService.EF_DG2)
            val dg2 = LDSFileUtil.getLDSFile(PassportService.EF_DG2, stream) as org.jmrtd.lds.icao.DG2File
            stream.close()

            val faceInfos = dg2.faceInfos
            Log.i("NfcReader", "┌─────────────────────────────────────┐")
            Log.i("NfcReader", "│  DG2 - PORTRAIT PHOTO               │")
            Log.i("NfcReader", "├─────────────────────────────────────┤")
            Log.i("NfcReader", "│ FaceInfo count  : ${faceInfos.size}")

            var photoBytes: ByteArray? = null

            faceInfos.forEachIndexed { index, faceInfo ->
                val faceImageInfos = faceInfo.faceImageInfos
                Log.i("NfcReader", "│ FaceInfo[$index] images: ${faceImageInfos.size}")

                faceImageInfos.forEach { imgInfo ->
                    photoBytes = imgInfo.imageInputStream.readBytes()
                    Log.i("NfcReader", "│ ├─ Image Size    : ${photoBytes?.size} bytes")
                    Log.i("NfcReader", "│ ├─ Width x Height: ${imgInfo.width} x ${imgInfo.height}")
                    Log.i("NfcReader", "│ ├─ Gender        : ${imgInfo.gender}")
                    Log.i("NfcReader", "│ ├─ Eye Color     : ${imgInfo.eyeColor}")
                    Log.i("NfcReader", "│ ├─ Hair Color    : ${imgInfo.hairColor}")
                    Log.i("NfcReader", "│ ├─ Expression    : ${imgInfo.expression}")
                    Log.i("NfcReader", "│ ├─ Face Image Type: ${imgInfo.faceImageType}")
                    Log.i("NfcReader", "│ ├─ Source Type   : ${imgInfo.sourceType}")
                    Log.i("NfcReader", "│ ├─ Feature Points: ${imgInfo.featurePoints.size}")
                    Log.i("NfcReader", "│ └─ Image Format  : ${detectImageFormat(photoBytes)}")

                    val base64 = Base64.encodeToString(photoBytes, Base64.DEFAULT)
                    Log.i("NfcReader", "│ Photo Base64 (first 100 chars): ${base64.take(100)}...")
                }
            }

            Log.i("NfcReader", "└─────────────────────────────────────┘")
            photoBytes
        } catch (e: Exception) {
            Log.e("NfcReader", "❌ DG2 failed: ${e.message}")
            null
        }
    }

    // ═══════════════════════════════════════════════════════
    // DG11: ADDITIONAL PERSONAL DETAILS
    // ═══════════════════════════════════════════════════════
    private fun readDG11(service: PassportService) {
        try {
            val stream = service.getInputStream(PassportService.EF_DG11)
            val dg11 = LDSFileUtil.getLDSFile(PassportService.EF_DG11, stream) as DG11File
            stream.close()

            Log.i("NfcReader", "┌─────────────────────────────────────┐")
            Log.i("NfcReader", "│  DG11 - ADDITIONAL PERSONAL DETAILS │")
            Log.i("NfcReader", "├─────────────────────────────────────┤")
            Log.i("NfcReader", "│ Place of Birth  : ${dg11.placeOfBirth ?: "N/A"}")
            Log.i("NfcReader", "│ Profession      : ${dg11.profession ?: "N/A"}")
            Log.i("NfcReader", "│ Title           : ${dg11.title ?: "N/A"}")
            Log.i("NfcReader", "│ Personal Summary: ${dg11.personalSummary ?: "N/A"}")
            Log.i("NfcReader", "│ Proof of Citizenship: ${dg11.proofOfCitizenship ?: "N/A"}")
            Log.i("NfcReader", "│ Other Valid TDs : ${dg11.otherValidTDNumbers?.joinToString() ?: "N/A"}")
            Log.i("NfcReader", "│ Custody Info    : ${dg11.custodyInformation ?: "N/A"}")
            Log.i("NfcReader", "└─────────────────────────────────────┘")
        } catch (e: Exception) {
            Log.w("NfcReader", "⚠️ DG11 not available: ${e.message}")
        }
    }

    // ═══════════════════════════════════════════════════════
    // DG12: ADDITIONAL DOCUMENT DETAILS
    // ═══════════════════════════════════════════════════════
    private fun readDG12(service: PassportService) {
        try {
            val stream = service.getInputStream(PassportService.EF_DG12)
            val dg12 = LDSFileUtil.getLDSFile(PassportService.EF_DG12, stream) as DG12File
            stream.close()

            Log.i("NfcReader", "┌─────────────────────────────────────┐")
            Log.i("NfcReader", "│  DG12 - DOCUMENT DETAILS            │")
            Log.i("NfcReader", "├─────────────────────────────────────┤")
            Log.i("NfcReader", "│ Issuing Authority : ${dg12.issuingAuthority ?: "N/A"}")
            Log.i("NfcReader", "│ Date of Issue     : ${dg12.dateOfIssue ?: "N/A"}")
            Log.i("NfcReader", "│ Endorsements      : ${dg12.endorsementsAndObservations ?: "N/A"}")
            Log.i("NfcReader", "│ Tax/Exit Reqs     : ${dg12.taxOrExitRequirements ?: "N/A"}")
            Log.i("NfcReader", "│ Personalization SN: ${dg12.personalizationSystemSerialNumber ?: "N/A"}")
            Log.i("NfcReader", "└─────────────────────────────────────┘")
        } catch (e: Exception) {
            Log.w("NfcReader", "⚠️ DG12 not available: ${e.message}")
        }
    }

    // ═══════════════════════════════════════════════════════
    // DG14: SECURITY OPTIONS
    // ═══════════════════════════════════════════════════════
    private fun readDG14(service: PassportService) {
        try {
            val stream = service.getInputStream(PassportService.EF_DG14)
            val dg14 = LDSFileUtil.getLDSFile(PassportService.EF_DG14, stream) as DG14File
            stream.close()

            Log.i("NfcReader", "┌─────────────────────────────────────┐")
            Log.i("NfcReader", "│  DG14 - SECURITY OPTIONS            │")
            Log.i("NfcReader", "├─────────────────────────────────────┤")
            Log.i("NfcReader", "│ Security Infos  : ${dg14.securityInfos.size}")
            dg14.securityInfos.forEachIndexed { i, info ->
                Log.i("NfcReader", "│ [$i] ${info.javaClass.simpleName}: ${info.objectIdentifier}")
            }
            Log.i("NfcReader", "└─────────────────────────────────────┘")
        } catch (e: Exception) {
            Log.w("NfcReader", "⚠️ DG14 not available: ${e.message}")
        }
    }

    // ═══════════════════════════════════════════════════════
    // DG15: ACTIVE AUTHENTICATION
    // ═══════════════════════════════════════════════════════
    private fun readDG15(service: PassportService) {
        try {
            val stream = service.getInputStream(PassportService.EF_DG15)
            val dg15 = LDSFileUtil.getLDSFile(PassportService.EF_DG15, stream) as DG15File
            stream.close()

            Log.i("NfcReader", "┌─────────────────────────────────────┐")
            Log.i("NfcReader", "│  DG15 - ACTIVE AUTHENTICATION       │")
            Log.i("NfcReader", "├─────────────────────────────────────┤")
            Log.i("NfcReader", "│ Public Key Algorithm: ${dg15.publicKey.algorithm}")
            Log.i("NfcReader", "│ Public Key Format   : ${dg15.publicKey.format}")
            Log.i("NfcReader", "│ Public Key Length   : ${dg15.publicKey.encoded.size} bytes")
            Log.i("NfcReader", "└─────────────────────────────────────┘")
        } catch (e: Exception) {
            Log.w("NfcReader", "⚠️ DG15 not available: ${e.message}")
        }
    }

    // ═══════════════════════════════════════════════════════
    // SOD: DOCUMENT SIGNATURE
    // ═══════════════════════════════════════════════════════
    private fun readSOD(service: PassportService) {
        try {
            val stream = service.getInputStream(PassportService.EF_SOD)
            // Read raw bytes instead of parsing SODFile to avoid BC crash
            val sodBytes = stream.readBytes()
            stream.close()

            Log.i("NfcReader", "┌─────────────────────────────────────┐")
            Log.i("NfcReader", "│  SOD - DOCUMENT SIGNATURE           │")
            Log.i("NfcReader", "├─────────────────────────────────────┤")
            Log.i("NfcReader", "│ SOD Size          : ${sodBytes.size} bytes")
            Log.i("NfcReader", "│ SOD Hex (first 32): ${sodBytes.take(32).joinToString("") { "%02x".format(it) }}")
            Log.i("NfcReader", "└─────────────────────────────────────┘")
        } catch (e: Exception) {
            Log.w("NfcReader", "⚠️ SOD not available: ${e.message}")
        }
    }

    private fun detectImageFormat(bytes: ByteArray?): String {
        if (bytes == null || bytes.size < 4) return "Unknown"
        return when {
            bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() -> "JPEG"
            bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() -> "PNG"
            bytes[0] == 0x00.toByte() && bytes[1] == 0x00.toByte() -> "JPEG2000"
            else -> "Unknown"
        }
    }
}