package com.example.ocr_v3.data.repository

import com.example.ocr_v3.data.local.CardDao
import com.example.ocr_v3.data.local.CardEntity
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.model.ScanType
import com.example.ocr_v3.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CardRepositoryImpl @Inject constructor(private val dao : CardDao) : CardRepository{
    override suspend fun insertCard(card: Card) {
        val entity = CardEntity(
            firstName = card.firstName,
            lastName = card.lastName ,
            birthDate = card.birthDate ,
            expirationDate = card.expirationDate ,
            documentNumber = card.documentNumber,
            numId = card.numId ,
            address = card.address,
            faceImagePath = card.faceImagePath,
            scanType = card.scanType.toString()
        )
        dao.insertCard(entity)
    }

    override suspend fun deleteCard(card: Card) {
        val entity = CardEntity(
            id = card.id,
            firstName = card.firstName,
            lastName = card.lastName,
            birthDate = card.birthDate,
            expirationDate = card.expirationDate ,
            documentNumber = card.documentNumber,
            numId = card.numId ,
            address = card.address,
            faceImagePath = card.faceImagePath,
            scanType = card.scanType.toString()
        )
        dao.deleteCard(entity)
    }

    override fun getCard(cardId : Int): Flow<Card> {
        return dao.getCard(cardId = cardId).map { entity ->
            Card(
                id = entity.id ,
                firstName = entity.firstName ,
                lastName = entity.lastName ,
                birthDate = entity.birthDate ,
                expirationDate = entity.expirationDate ,
                documentNumber = entity.documentNumber,
                numId = entity.numId ,
                address = entity.address,
                faceImagePath = entity.faceImagePath,
                scanType = ScanType.valueOf(entity.scanType)
            )
        }
    }

    override fun getAllCards(): Flow<List<Card>> {
        return dao.getCards().map { entities ->
            entities.map { entity ->
                Card(
                    id = entity.id,
                    firstName = entity.firstName,
                    lastName = entity.lastName,
                    birthDate = entity.birthDate,
                    expirationDate = entity.expirationDate ,
                    documentNumber = entity.documentNumber,
                    numId = entity.numId,
                    address = entity.address,
                    faceImagePath = entity.faceImagePath,
                    scanType = ScanType.valueOf(entity.scanType)
                )
            }

        }
    }
}