package com.example.ocr_v3.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocr_v3.domain.model.Card
import com.example.ocr_v3.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel  @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {
//     val cards = cardRepository.getAllCards()



    //search bar
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    val cards = searchText
        .combine(cardRepository.getAllCards()) { text, cardList ->
            if (text.isBlank()) {
                cardList
            } else {
                cardList.filter {
                    it.doesMatchQuery(text)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun onSearchTextChange(text: String){
        _searchText.value = text
    }

    fun deleteCard(card: Card){
        viewModelScope.launch {
            cardRepository.deleteCard(card)
        }
    }

}