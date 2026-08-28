package com.example.ocr_v3.domain.usecase

import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.model.ScanType

class ParseCardUseCase {

    operator fun invoke(textToTrim: String): Card {
        val lines = textToTrim.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        var numId = ""
        var documentNumber = ""  // NEW: document number from after IDMAR
        var firstName = ""
        var lastName = ""
        var birthDate = ""
        var expirationDate = ""
        var address = ""

        // 1- Address
        val addressRegex = Regex("""(?i)^[aá]d?r?[eaos]{1,4}e?\s*[:.-]?\s*(.*)$""")
        for (line in lines) {
            val match = addressRegex.find(line)
            if (match != null) {
                address = match.groupValues[1].trim()
                break
            }
        }

        // MRZ lines: handle both "I<MAR" and "IDMAR" (OCR misread)
        val mrzLines = lines.filter {
            it.startsWith("I<MAR") || it.startsWith("IDMAR") || it.count { char -> char == '<' } > 5
        }

        if (mrzLines.size >= 3) {
            // Remove spaces
            val mrz1 = mrzLines[mrzLines.size - 3].replace(" ", "")
            val mrz2 = mrzLines[mrzLines.size - 2].replace(" ", "")
            val mrz3 = mrzLines[mrzLines.size - 1].replace(" ", "")

            // --- Extract Document Number (From Line 1) ---
            // Format: IDMAR[DOCUMENT_NUMBER]<[CHECK_DIGIT][ID_NUMBER]<<<<<<<
            // Example: IDMARFHE4N2I9<2AB123456<<<<<<<
            // Document number is "FHE4N2I9"
            if (mrz1.startsWith("IDMAR")) {
                documentNumber = mrz1.substringAfter("IDMAR").substringBefore('<')
            } else if (mrz1.startsWith("I<MAR")) {
                // Fallback: standard format I<MAR
                documentNumber = mrz1.substringAfter("I<MAR").substringBefore('<')
            }

            // --- Extract ID Number (From Line 1) ---
            // The ID number is after the check digit: <[CHECK_DIGIT][ID_NUMBER]<
            // Example: <2AB123456< -> ID number is "AB123456"
            val idRegex = Regex("""<(\d)([A-Z0-9\s]+)<""")
            val match = idRegex.find(mrz1)
            if (match != null) {
                numId = match.groupValues[2]
            }

            // --- Extract Dates (From Line 2) ---
            if (mrz2.length >= 15) {
                val rawDob = mrz2.substring(0, 6)
                val rawExp = mrz2.substring(8, 14)

                birthDate = formatMrzDate(rawDob, isBirthDate = true)
                expirationDate = formatMrzDate(rawExp, isBirthDate = false)
            }

            // --- Extract Names (From Line 3) ---
            // Handles << or K< (OCR misread)
            val nameParts = mrz3.split(Regex("""[<Kk]<"""))
            if (nameParts.isNotEmpty()) {
                lastName = nameParts[0].replace("<", " ").trim()
                if (nameParts.size > 1) {
                    firstName = nameParts[1].replace("<", " ").trim()
                }
            }
        }

        // Fix OCR issue: '0' (zero) is often read instead of 'O' in names
        firstName = firstName.replace('0', 'O')
        lastName = lastName.replace('0', 'O')

        return Card(
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            expirationDate = expirationDate,
            numId = numId,
            documentNumber = documentNumber,  // NEW field
            address = address,
            scanType = ScanType.OCR
        )
    }


}