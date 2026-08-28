package com.example.ocr_v3.domain.model


enum class ScanType{
    OCR ,
    NFC
}

data class Card (
    val id : Int = 0 ,
    val firstName : String ,
    val lastName : String ,
    val birthDate : String ,
    val expirationDate : String ,
    val documentNumber: String ,
    val numId : String ,
    val address : String? = null ,
    val faceImagePath : String? = null ,
    val scanType: ScanType
){
    fun doesMatchQuery(query : String) : Boolean{
        val matchingCombination = listOf(
            "$firstName $lastName",
            "$lastName $firstName"
        )
        return matchingCombination.any(){
            it.contains(query , ignoreCase = true)
        }
    }
}