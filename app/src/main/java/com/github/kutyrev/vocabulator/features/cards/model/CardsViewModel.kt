package com.github.kutyrev.vocabulator.features.cards.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.app.LIST_ID_PARAM_NAME
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.EMPTY_CARD
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

private const val ZERO_CARD_POSITION_OFFSET = 0f
private const val NEXT_CARD_OFFSET = 1f
private const val PREVIOUS_CARD_OFFSET = -1f

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
    var isForeignLangFirst = mutableStateOf(true)
        private set
    var isRandomCards = mutableStateOf(false)
        private set
    private val _messages = MutableStateFlow<CardsMessages?>(null)
    val messages = _messages.asStateFlow()

    init {
        savedStateHandle.get<String>(LIST_ID_PARAM_NAME)?.let {
            listId.value = it.toInt()
        }

        viewModelScope.launch {
            storageRepository.getCards(listId.value).collect {
                _cards.value = it
                emitNewCard(ZERO_CARD_POSITION_OFFSET)
            }
        }
    }

    fun emitNewCard(offsetX: Float) {
        if (cards.value.isEmpty()) return

        val maxInd = cards.value.size * 2 - 2

        if (!isRandomCards.value) {

            if (offsetX > 0 && cardIndex > 0)
                cardIndex -= 1
            else if (offsetX < 0 && cardIndex < maxInd) {
                cardIndex += 1
            }
        } else {
            cardIndex = Random.nextInt(0, maxInd)
        }

        if (cardIndex < cards.value.size) {
            _card.value = cards.value[cardIndex]
            isForeignLangFirst.value = true
        } else {
            _card.value = cards.value[cardIndex - cards.value.size]
            isForeignLangFirst.value = false
        }
    }

    fun onChangeIsRandomCardsState(newState: Boolean) {
        isRandomCards.value = newState
    }

    fun onNextCardButtonPressed() {
        emitNewCard(NEXT_CARD_OFFSET)
    }

    fun onPreviousCardButtonPressed() {
        emitNewCard(PREVIOUS_CARD_OFFSET)
    }

    fun deleteWordCard() {
        viewModelScope.launch {
            storageRepository.deleteWordCards(listOf(card.value))
            emitNewCard(NEXT_CARD_OFFSET)
            _messages.emit(CardsMessages.DELETED)
        }
    }

    fun addWordInCommons() {
        viewModelScope.launch {
            val subtitlesUnit = storageRepository.getSubtitlesUnit(card.value.subtitleId)
            storageRepository.insertCommonWords(listOf(CommonWord(languageId = subtitlesUnit.origLangId, word = card.value.originalWord)))
            _messages.emit(CardsMessages.ADDEDINCOMMONS)
            storageRepository.deleteWordCards(listOf(card.value))
            emitNewCard(NEXT_CARD_OFFSET)
        }
    }
}

enum class CardsMessages(val messageId: Int) {
    DELETED(R.string.cards_scr_message_delete),
    ADDEDINCOMMONS(R.string.cards_scr_message_added_in_commons)
}
