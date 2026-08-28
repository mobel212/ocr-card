package com.example.ocr_v3.domain.usecase

fun formatMrzDate(yymmdd: String, isBirthDate: Boolean): String {
    if (yymmdd.length != 6 || yymmdd.contains("<") || yymmdd.any { !it.isDigit() }) {
        return yymmdd
    }

    val yearStr = yymmdd.substring(0, 2)
    val month = yymmdd.substring(2, 4)
    val day = yymmdd.substring(4, 6)

    val yearInt = yearStr.toIntOrNull() ?: return yymmdd

    val fullYear = if (isBirthDate) {
        if (yearInt > 30) "19$yearStr" else "20$yearStr"
    } else {
        "20$yearStr"
    }

    return "$day/$month/$fullYear"
}