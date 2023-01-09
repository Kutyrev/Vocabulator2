package com.github.kutyrev.vocabulator.features.cards.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.model.EMPTY_CARD
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LIST_ID_PARAM_NAME = "listId"

@HiltViewModel
class CardsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storageRepository: StorageRepository
) : ViewModel() {

    var listId = mutableStateOf(0)
    private var _cards: MutableStateFlow<List<WordCard>> = MutableStateFlow(listOf())
    val cards: StateFlow<List<WordCard>> = _cards
    var cardIndex: Int = 0
    private var _card = MutableStateFlow(EMPTY_CARD)
    val card: StateFlow<WordCard>
        get() = _card

    init {
        savedStateHandle.get<String>(LIST_ID_PARAM_NAME)?.let {
            listId.value = it.toInt()
        }
        viewModelScope.launch {
            _cards.value = storageRepository.getCards(listId.value).first()
            emitNewCard(0f)
        }
    }

    fun emitNewCard(offsetX: Float) {
        if(cards.value.isEmpty()) return

        if(offsetX > 0 && cardIndex > 0)
            cardIndex -= 1
        else if(offsetX < 0 && cardIndex < cards.value.size - 1){
            cardIndex += 1
        }

        _card.value = cards.value[cardIndex]
    }
}
