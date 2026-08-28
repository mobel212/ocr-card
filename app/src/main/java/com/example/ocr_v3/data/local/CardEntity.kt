package com.example.ocr_v3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val firstName : String ,
    val lastName : String ,
    val birthDate : String ,
    val documentNumber: String ,
    val expirationDate : String ,
    val numId : String ,
    val address : String? ,
    val faceImagePath : String? ,
    val scanType : String
) {

}