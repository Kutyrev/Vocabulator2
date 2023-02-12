package com.github.kutyrev.vocabulator.features.commons.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonsViewModel @Inject constructor(private val storageRepository: StorageRepository) :
    ViewModel() {

    private var _language = mutableStateOf(Language.EN)
    val language: MutableState<Language>
        get() = _language

    var words = mutableStateListOf<EditableCommonWord>()
        private set

    var searchText = mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            storageRepository.getCommonWords(language.value)
                .forEach { words.add(EditableCommonWord(it.id, it.languageId, it.word)) }
        }
    }

    fun onLanguageChange(newLanguage: Language) {
        _language.value = newLanguage
    }

    fun onSearchTextChange(newText: String) {
        searchText.value = newText
    }

    fun onWordCheckedStateChange(word: EditableCommonWord, checked: Boolean) {
        val wordIndex = words.indexOf(word)
        words[wordIndex] = words[wordIndex].copy(checked = checked)
    }

    fun onOkButtonPressed() {
        val commonWordsToDelete = mutableListOf<CommonWord>()
        for (word in words) {
            if (!word.checked) {
                commonWordsToDelete.add(CommonWord(word.id, word.languageId, word.word))
            }
        }

        if(commonWordsToDelete.isNotEmpty()) {
            viewModelScope.launch {
                storageRepository.deleteCommonWords(commonWordsToDelete)
            }
        }
    }
}
