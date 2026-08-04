package com.example.ocr_v3.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.ocr_v3.domain.model.Card
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Upsert
    suspend fun insertCard(card : CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("SELECT * FROM cards ")
    fun getCards() : Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :cardId ")
    fun getCard(cardId : Int) : Flow<CardEntity>
}