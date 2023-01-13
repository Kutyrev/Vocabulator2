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

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) : ViewModel() {

    private var _numberOfWordsForLoad: MutableStateFlow<Int> = MutableStateFlow(COUNT_OF_WORDS_INITIAL_VALUE)
    val numberOfWordsForLoad: StateFlow<Int> = _numberOfWordsForLoad

    fun saveCountOfWordsForLoad() {
        viewModelScope.launch { settingsRepository.setWordsForLoadCount(_numberOfWordsForLoad.value) }
    }

    fun startCollectingNumberOfWordsForLoad() {
        viewModelScope.launch {
            settingsRepository.getWordsForLoadCount().collect {
                _numberOfWordsForLoad.value = it
            }
        }
    }

    fun changeNumberOfWordsValue(newValue: Int) {
        _numberOfWordsForLoad.value = newValue
    }
}