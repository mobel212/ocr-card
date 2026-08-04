package com.example.ocr_v3.domain.repository

import com.example.ocr_v3.domain.model.Card
import dagger.Provides
import kotlinx.coroutines.flow.Flow

interface CardRepository{

    suspend fun insertCard(card: Card)

    suspend fun deleteCard(card: Card)

    fun getCard(cardId : Int) : Flow<Card>

    fun getAllCards() : Flow<List<Card>>
}