package com.github.kutyrev.vocabulator.features.editsub.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.app.LIST_ID_PARAM_NAME
import com.github.kutyrev.vocabulator.model.EMPTY_SUBS_ID
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
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

    private var _subtitlesUnit: MutableState<SubtitlesUnit?> = mutableStateOf(null)
    val subtitlesUnit: MutableState<SubtitlesUnit?>
        get() = _subtitlesUnit

    private var _subsLanguage: MutableState<Language> = mutableStateOf(Language.EN)
    val subsLanguage: MutableState<Language>
        get() = _subsLanguage

    private var _langOfTranslation: MutableState<Language> = mutableStateOf(Language.EN)
    val langOfTranslation: MutableState<Language>
        get() = _langOfTranslation

    private var _subtitlesName: MutableState<String> = mutableStateOf("")
    val subtitlesName: MutableState<String>
        get() = _subtitlesName

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
            viewModelScope.launch {
                _subtitlesUnit.value = storageRepository.getSubtitlesUnit(listId)
                if (_subtitlesUnit.value != null) {
                    _subsLanguage.value = Language.values()[_subtitlesUnit.value!!.origLangId]
                    _subtitlesName.value = _subtitlesUnit.value!!.name
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

    fun onSubtitleNameChange(newSubtitleName: String) {
        _subtitlesName.value = newSubtitleName
    }

    fun onSubtitlesLanguageChange(language: Language) {
        _subsLanguage.value = language

    }

    fun onTargetLanguageChange(language: Language) {
        _langOfTranslation.value = language

    }

    fun onOkButtonPressed() {
        var mainSubtitleInfoChanged = false

        if(_subtitlesUnit.value != null) {
            if (_subtitlesUnit.value!!.name != _subtitlesName.value) {
                mainSubtitleInfoChanged = true
                _subtitlesUnit.value!!.name = _subtitlesName.value
            }
            if(_subtitlesUnit.value!!.origLangId != _subsLanguage.value.ordinal) {
                mainSubtitleInfoChanged = true
                _subtitlesUnit.value!!.origLangId = _subsLanguage.value.ordinal
            }
        }
    }
}
