package com.github.kutyrev.vocabulator.features.settings.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.repository.datastore.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val COUNT_OF_WORDS_INITIAL_VALUE = 100
private const val IS_LOAD_PHRASES_EXAMPLES = false

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    private var _numberOfWordsForLoad: MutableStateFlow<Int> = MutableStateFlow(
        COUNT_OF_WORDS_INITIAL_VALUE
    )
    val numberOfWordsForLoad: StateFlow<Int> = _numberOfWordsForLoad

    private var _loadPhrasesExamples: MutableStateFlow<Boolean> = MutableStateFlow(
        IS_LOAD_PHRASES_EXAMPLES
    )
    val loadPhrasesExamples: StateFlow<Boolean> = _loadPhrasesExamples

    fun onSaveButtonClick() {
        saveCountOfWordsForLoad()
        saveIsLoadPhrases()
    }

    fun startCollectingNumberOfWordsForLoad() {
        viewModelScope.launch {
            settingsRepository.getWordsForLoadCount().collect {
                _numberOfWordsForLoad.value = it
            }
        }
    }

    fun startCollectingLoadPhrases() {
        viewModelScope.launch {
            settingsRepository.getLoadPhrasesExamples().collect() {
                _loadPhrasesExamples.value = it
            }
        }
    }

    fun changeNumberOfWordsValue(newValue: Int) {
        _numberOfWordsForLoad.value = newValue
    }

    fun changeIsSetLoadPhrases(newValue: Boolean) {
        _loadPhrasesExamples.value = newValue
    }

    private fun saveCountOfWordsForLoad() {
        viewModelScope.launch { settingsRepository.setWordsForLoadCount(_numberOfWordsForLoad.value) }
    }

    private fun saveIsLoadPhrases() {
        viewModelScope.launch { settingsRepository.setLoadPhrasesExamples(_loadPhrasesExamples.value) }
    }
}
