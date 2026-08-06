package com.example.ocr_v3.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel  @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {
     val cards = cardRepository.getAllCards()

    fun deleteCard(card: Card){
        viewModelScope.launch {
            cardRepository.deleteCard(card)
        }
    }

}