package com.github.kutyrev.vocabulator.features.editsub.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.app.LIST_ID_PARAM_NAME
import com.github.kutyrev.vocabulator.model.EMPTY_SUBS_ID
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.repository.StorageRepository
import com.github.kutyrev.vocabulator.repository.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSubViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storageRepository: StorageRepository,
    private val translationRepository: TranslationRepository
) : ViewModel() {

    private var listId: Int = EMPTY_SUBS_ID
    private var _words: MutableStateFlow<List<WordCard>> = MutableStateFlow(listOf())
    val words: StateFlow<List<WordCard>> = _words

    init {
        savedStateHandle.get<String>(LIST_ID_PARAM_NAME)?.let {
            listId = it.toInt()
        }

        if (listId != EMPTY_SUBS_ID) {
            viewModelScope.launch {
                storageRepository.getCards(listId).collect {
                    _words.value = it
                }
            }
        }
    }

    fun onOrigWordChange(newValue: String, word: WordCard) {
        word.originalWord = newValue
    }

    fun onTranslationChange(newValue: String, word: WordCard) {
        word.translatedWord = newValue
    }

    fun translateWords(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language
    ) {
        viewModelScope.launch {
            translationRepository.getTranslation(
                words,
                origLanguage,
                translationLanguage
            )
        }
    }
}
