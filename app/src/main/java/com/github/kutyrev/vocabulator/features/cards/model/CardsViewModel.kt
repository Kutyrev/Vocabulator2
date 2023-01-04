package com.github.kutyrev.vocabulator.features.cards.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val LIST_ID_PARAM_NAME = "listId"

@HiltViewModel
class CardsViewModel @Inject constructor(savedStateHandle: SavedStateHandle): ViewModel() {
    var listId = mutableStateOf(0)
    init {
        savedStateHandle.get<String>(LIST_ID_PARAM_NAME)?.let {
            listId.value = it.toInt()
        }
    }
}
