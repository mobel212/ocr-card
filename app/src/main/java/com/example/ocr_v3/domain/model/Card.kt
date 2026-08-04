package com.example.ocr_v3.domain.model



data class Card (
    val id : Int = 0 ,
    val firstName : String ,
    val lastName : String ,
    val birthDate : String ,
    val expirationDate : String ,
    val numId : String ,
    val address : String
)