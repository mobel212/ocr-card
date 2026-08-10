package com.example.ocr_v3.domain.usecase

import com.example.ocr_v3.domain.model.Card

class ParseCardUseCase {

    operator fun invoke(textToTrim: String): Card {
        val lines = textToTrim.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        var numId = ""
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

        val mrzLines = lines.filter {
            it.startsWith("I<MAR") || it.count { char -> char == '<' } > 5
        }

        if (mrzLines.size >= 3) {
            // Remove spaces
            val mrz1 = mrzLines[mrzLines.size - 3].replace(" ", "")
            val mrz2 = mrzLines[mrzLines.size - 2].replace(" ", "")
            val mrz3 = mrzLines[mrzLines.size - 1].replace(" ", "")

            val idRegex = Regex("""<(\d)([A-Z0-9\s]+)<""")
            val match = idRegex.find(mrz1)
            if (match != null) {
                // group 1 is the digit, group 2 is the actual ID number
                numId = match.groupValues[2]
            }

            // --- Extract Dates (From Line 2) ---
            // Format: [6-digit DOB][Check][Sex][6-digit Expiry][Check]...
            if (mrz2.length >= 15) {
                val rawDob = mrz2.substring(0, 6)      // positions 1-6
                val rawExp = mrz2.substring(8, 14)     // positions 9-14

                birthDate = formatMrzDate(rawDob, isBirthDate = true)
                expirationDate = formatMrzDate(rawExp, isBirthDate = false)
            }

            // --- Extract Names (From Line 3) ---
            // Format: LASTNAME<<FIRSTNAME<<...
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
            address = address
        )
    }

    private fun formatMrzDate(yymmdd: String, isBirthDate: Boolean): String {
        if (yymmdd.length != 6 || yymmdd.contains("<") || yymmdd.any { !it.isDigit() }) {
            return yymmdd
        }

        val yearStr = yymmdd.substring(0, 2)
        val month = yymmdd.substring(2, 4)
        val day = yymmdd.substring(4, 6)

        val yearInt = yearStr.toIntOrNull() ?: return yymmdd

        // Birth: years > 30 → 19xx, else 20xx. Expiry: always 20xx.
        val fullYear = if (isBirthDate) {
            if (yearInt > 30) "19$yearStr" else "20$yearStr"
        } else {
            "20$yearStr"
        }

        return "$day/$month/$fullYear"
    }
}