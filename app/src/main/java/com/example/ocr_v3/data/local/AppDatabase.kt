package com.example.ocr_v3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [CardEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){
    abstract val dao : CardDao
}