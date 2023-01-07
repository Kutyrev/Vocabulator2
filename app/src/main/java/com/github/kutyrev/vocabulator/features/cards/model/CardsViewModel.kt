package com.github.kutyrev.vocabulator.features.cards.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        savedStateHandle.get<String>(LIST_ID_PARAM_NAME)?.let {
            listId.value = it.toInt()
        }
        viewModelScope.launch { _cards.value = storageRepository.getCards(listId.value).first() }
    }
}
