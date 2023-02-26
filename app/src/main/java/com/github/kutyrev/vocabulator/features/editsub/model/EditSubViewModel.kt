package com.github.kutyrev.vocabulator.features.editsub.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.app.LIST_ID_PARAM_NAME
import com.github.kutyrev.vocabulator.model.*
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import com.github.kutyrev.vocabulator.repository.translator.TranslationCallback
import com.github.kutyrev.vocabulator.repository.translator.TranslationRepository
import com.github.kutyrev.vocabulator.ui.components.CheckableWord
import com.github.kutyrev.vocabulator.ui.components.TRANSLATION_DELIMITER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSubViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storageRepository: StorageRepository,
    private val translationRepository: TranslationRepository
) : ViewModel(), TranslationCallback {

    private var listId: Int = EMPTY_SUBS_ID
    private val _words = mutableStateListOf<EditableWordCard>()
    val words: List<EditableWordCard> = _words

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

    private var _uncheckedToDict: MutableState<Boolean> = mutableStateOf(false)
    val uncheckedToDict: MutableState<Boolean>
        get() = _uncheckedToDict

    var isEdit = mutableStateOf(false)
        private set

    val checkableWords = mutableStateListOf<CheckableWord>()

    private var editableWord: WordCard? = null

    init {
        savedStateHandle.get<String>(LIST_ID_PARAM_NAME)?.let {
            listId = it.toInt()
        }

        if (listId != EMPTY_SUBS_ID) {
            viewModelScope.launch {
                storageRepository.getCards(listId).collect { wordsList ->
                    _words.addAll(wordsList.map {
                        EditableWordCard(
                            id = it.id,
                            subtitleId = it.subtitleId,
                            originalWord = it.originalWord,
                            translatedWord = it.translatedWord
                        )
                    })
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
        val wordIndex = _words.indexOf(word)
        _words[wordIndex] = _words[wordIndex].copy(originalWord = newValue, changed = true)
    }

    fun onTranslationChange(newValue: String, word: WordCard) {
        val wordIndex = _words.indexOf(word)
        _words[wordIndex] = _words[wordIndex].copy(translatedWord = newValue, changed = true)
    }

    fun translateWords() {
        viewModelScope.launch {
            translationRepository.getTranslation(
                words,
                subsLanguage.value,
                langOfTranslation.value,
                this@EditSubViewModel
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

    fun onWordCheckedStateChange(word: EditableWordCard, checked: Boolean) {
        val wordIndex = _words.indexOf(word)
        _words[wordIndex] = _words[wordIndex].copy(checked = checked)
    }

    fun onChangeUncheckedToDict(newValue: Boolean) {
        _uncheckedToDict.value = newValue
    }

    fun onOkButtonPressed() {
        updateMainInfo()
        updateWords()
        updateCommonWords()
    }

    override fun receiveTranslation(translatedWords: List<WordCard>) {
        for (i in _words.indices) {
            _words[i] = _words[i].copy(changed = true)
        }
    }

    fun onTranslationClick(word: WordCard) {
        checkableWords.clear()
        editableWord = word
        word.translatedWord.split(TRANSLATION_DELIMITER).forEach {
            checkableWords.add(CheckableWord(checked = true, word = it))
        }
        isEdit.value = true
    }

    fun onIsEditStateChange(show: Boolean) {
        isEdit.value = show
    }

    fun onChangeTranslation(newTranslation: String) {
        isEdit.value = false
        val wordIndex = _words.indexOf(editableWord)
        _words[wordIndex] = _words[wordIndex].copy(translatedWord = newTranslation, changed = true)
    }

    private fun updateCommonWords() {
        val newCommonWords: MutableList<CommonWord> = mutableListOf()

        if (uncheckedToDict.value) {
            words.forEach {
                if (!it.checked) {
                    newCommonWords.add(
                        CommonWord(
                            languageId = subsLanguage.value.ordinal,
                            word = it.originalWord
                        )
                    )
                }
            }

            if (newCommonWords.isNotEmpty()) {
                viewModelScope.launch {
                    storageRepository.insertCommonWords(newCommonWords)
                }
            }
        }
    }

    private fun updateWords() {
        val changedWords: MutableList<WordCard> = mutableListOf()
        val wordsToDelete: MutableList<WordCard> = mutableListOf()

        words.forEach {
            if (it.changed && it.checked) {
                changedWords.add(WordCard(it.id, it.subtitleId, it.originalWord, it.translatedWord))
            }
            if (!it.checked) {
                wordsToDelete.add(
                    WordCard(
                        it.id,
                        it.subtitleId,
                        it.originalWord,
                        it.translatedWord
                    )
                )
            }
        }

        if (changedWords.isNotEmpty()) {
            viewModelScope.launch {
                storageRepository.updateWordCards(changedWords)
            }
        }

        if (wordsToDelete.isNotEmpty()) {
            viewModelScope.launch {
                storageRepository.deleteWordCards(wordsToDelete)
            }
        }
    }

    private fun updateMainInfo() {
        var mainSubtitleInfoChanged = false

        if (_subtitlesUnit.value != null) {
            if (_subtitlesUnit.value!!.name != _subtitlesName.value) {
                mainSubtitleInfoChanged = true
                _subtitlesUnit.value!!.name = _subtitlesName.value
            }
            if (_subtitlesUnit.value!!.origLangId != _subsLanguage.value.ordinal) {
                mainSubtitleInfoChanged = true
                _subtitlesUnit.value!!.origLangId = _subsLanguage.value.ordinal
            }
            if (mainSubtitleInfoChanged) {
                viewModelScope.launch {
                    storageRepository.updateSubtitles(_subtitlesUnit.value!!)
                }
            }
        }
    }
}
