package com.github.kutyrev.vocabulator.features.editsub.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.app.LIST_ID_PARAM_NAME
import com.github.kutyrev.vocabulator.model.*
import com.github.kutyrev.vocabulator.repository.file.FileRepository
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import com.github.kutyrev.vocabulator.repository.translator.TranslationCallback
import com.github.kutyrev.vocabulator.repository.translator.TranslationRepository
import com.github.kutyrev.vocabulator.repository.translator.TranslationResultStatus
import com.github.kutyrev.vocabulator.ui.components.CheckableWord
import com.github.kutyrev.vocabulator.ui.components.TRANSLATION_DELIMITER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditSubViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storageRepository: StorageRepository,
    private val translationRepository: TranslationRepository,
    private val fileRepository: FileRepository
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

    private val _messages = MutableStateFlow<EditCardsMessages?>(null)
    val messages = _messages.asStateFlow()

    val isFirstLoad = fileRepository.sortedWords.isNotEmpty()

    private var _showLoadingDialog: MutableState<Boolean> = mutableStateOf(false)
    val showLoadingDialog: MutableState<Boolean>
        get() = _showLoadingDialog

    private var _showAddNewWordCardDialog: MutableState<Boolean> = mutableStateOf(false)
    val showAddNewWordCardDialog: MutableState<Boolean>
        get() = _showAddNewWordCardDialog

    init {
        savedStateHandle.get<String>(LIST_ID_PARAM_NAME)?.let {
            listId = it.toInt()
        }

        if (listId != EMPTY_SUBS_ID) {
            viewModelScope.launch {
                _subtitlesUnit.value = storageRepository.getSubtitlesUnit(listId)
                if (_subtitlesUnit.value != null) {
                    _subsLanguage.value = Language.values()[_subtitlesUnit.value!!.origLangId]
                    if(isFirstLoad) {
                        when(Locale.getDefault().language.uppercase()) {
                            Language.EN.name -> _langOfTranslation.value = Language.EN
                            Language.RU.name -> _langOfTranslation.value = Language.RU
                            Language.FR.name -> _langOfTranslation.value = Language.FR
                            Language.IT.name -> _langOfTranslation.value = Language.IT
                        }
                    } else {
                        _langOfTranslation.value =
                            Language.values()[_subtitlesUnit.value!!.transLangId]
                    }
                    _subtitlesName.value = _subtitlesUnit.value!!.name
                    loadWords()
                }
            }
        }
    }

    fun onOrigWordChange(newValue: String, word: EditableWordCard) {
        val wordIndex = _words.indexOf(word)
        _words[wordIndex] = _words[wordIndex].copy(originalWord = newValue, changed = true)
    }

    fun onTranslationChange(newValue: String, word: EditableWordCard) {
        val wordIndex = _words.indexOf(word)
        _words[wordIndex] = _words[wordIndex].copy(translatedWord = newValue, changed = true)
    }

    fun translateWords() {
        viewModelScope.launch {
            translationRepository.getFirebaseTranslation(
                words.filter { it.checked },
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
        fileRepository.sortedWords = mapOf()
    }

    override fun receiveTranslation(
        translatedWords: List<WordCard>,
        translationResult: TranslationResultStatus
    ) {
        for (i in _words.indices) {
            _words[i] = _words[i].copy(changed = true)
        }

        viewModelScope.launch {
            when (translationResult) {
                TranslationResultStatus.Success -> _messages.emit(EditCardsMessages.SUCCESS)
                TranslationResultStatus.YandexGenericError -> _messages.emit(EditCardsMessages.YANDEX_GENERIC_ERROR)
                TranslationResultStatus.YandexNetworkError -> _messages.emit(EditCardsMessages.NETWORK_ERROR)
                TranslationResultStatus.FirebaseError -> _messages.emit(EditCardsMessages.FIREBASE_ERROR)
                TranslationResultStatus.FirebaseSuccess -> translationRepository.getYandexTranslation(
                    words.filter { it.checked && it.translatedWord.isEmpty() },
                    subsLanguage.value,
                    langOfTranslation.value,
                    this@EditSubViewModel
                )
            }
        }
    }

    fun onTranslationClick(word: EditableWordCard) {
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

    fun updateCommonsAndReloadFile() {
        _showLoadingDialog.value = true

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
        }

        viewModelScope.launch {
            if (newCommonWords.isNotEmpty()) {
                storageRepository.insertCommonWords(newCommonWords)
            }

            subtitlesUnit.value?.let {
                val newWords =
                    fileRepository.reparseSubtitles(it)
                storageRepository.deleteWordCards(it.wordCards)
                _words.clear()
                storageRepository.insertWordCards(newWords)

                _showLoadingDialog.value = false
            }
        }
    }

    fun addNewWordCard(origWord: String, translatedWord: String) {
        subtitlesUnit.value?.let {
            viewModelScope.launch {
                _words.clear()
                storageRepository.insertWordCards(
                    mutableListOf(
                        WordCard(
                            subtitleId = it.id,
                            originalWord = origWord,
                            translatedWord = translatedWord
                        )
                    )
                )
                setAddNewWordCardDialogVisibility(false)
                _messages.emit(EditCardsMessages.NEW_WORD_ADDED)
            }
        }
    }

    fun resetMessagesStatus() {
        viewModelScope.launch {
            _messages.emit(null)
        }
    }

    fun setAddNewWordCardDialogVisibility(isVisible: Boolean) {
        _showAddNewWordCardDialog.value = isVisible
    }

    private fun loadWords() {
        viewModelScope.launch {
            storageRepository.getCards(listId).collect { wordsList ->
                subtitlesUnit.value?.wordCards?.clear()
                subtitlesUnit.value?.wordCards?.addAll(wordsList)
                _words.addAll(wordsList.map {
                    EditableWordCard(
                        id = it.id,
                        subtitleId = it.subtitleId,
                        originalWord = it.originalWord,
                        translatedWord = it.translatedWord,
                        quantity = it.quantity,
                        phrase = it.phrase
                    )
                })
            }
        }
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
                changedWords.add(
                    WordCard(
                        id = it.id,
                        subtitleId = it.subtitleId,
                        originalWord = it.originalWord,
                        translatedWord = it.translatedWord,
                        quantity = it.quantity,
                        phrase = it.phrase
                    )
                )
            }
            if (!it.checked) {
                wordsToDelete.add(
                    WordCard(
                        id = it.id,
                        subtitleId = it.subtitleId,
                        originalWord = it.originalWord,
                        translatedWord = it.translatedWord,
                        quantity = it.quantity,
                        phrase = it.phrase
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
            if (_subtitlesUnit.value!!.transLangId != _langOfTranslation.value.ordinal) {
                mainSubtitleInfoChanged = true
                _subtitlesUnit.value!!.transLangId = _langOfTranslation.value.ordinal
            }
            if (mainSubtitleInfoChanged) {
                viewModelScope.launch {
                    storageRepository.updateSubtitles(_subtitlesUnit.value!!)
                }
            }
        }
    }

    enum class EditCardsMessages(val messageId: Int) {
        SUCCESS(R.string.edit_message_success),
        NETWORK_ERROR(R.string.edit_message_network_error),
        YANDEX_GENERIC_ERROR(R.string.edit_message_yandex_generic_error),
        FIREBASE_ERROR(R.string.edit_message_firebase_error),
        NEW_WORD_ADDED(R.string.edit_message_word_added)
    }
}
