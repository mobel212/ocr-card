package com.example.ocr_v3.presentation

import com.example.ocr_v3.data.local.CardEntity
import com.example.ocr_v3.domain.model.Card


//UI Intent class (often called an MVI State or Event class


sealed interface CardEvent {
    object SaveCard : CardEvent
    data class SetFirstName(val firstName: String) : CardEvent
    data class SetLastName(val lastName: String) : CardEvent
    data class SetBirthDate(val birthDate: String) : CardEvent
    data class SetNumId(val numId: String) : CardEvent
    data class SetAddress(val address: String) : CardEvent

    object ShowDialog : CardEvent

    data class DeleteCard(val cardEntity: CardEntity ) : CardEvent
}