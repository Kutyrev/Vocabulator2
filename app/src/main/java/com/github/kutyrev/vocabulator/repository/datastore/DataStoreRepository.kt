package com.github.kutyrev.vocabulator.repository.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PROP_WORDS_FOR_LOAD_COUNT = "words_for_load_count"
private const val PROP_LOAD_PHRASES_EXAMPLES = "load_phrases_examples"
private const val PROP_IS_FIRST_RUN = "is_first_run"
private const val DEFAULT_LOAD_WORDS_NUMBER = 100

class DataStoreRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    SettingsRepository {

    override fun getWordsForLoadCount(): Flow<Int> {
        return dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[intPreferencesKey(PROP_WORDS_FOR_LOAD_COUNT)] ?: DEFAULT_LOAD_WORDS_NUMBER
            }
    }

    override suspend fun setWordsForLoadCount(newWordsCount: Int) {
        dataStore.edit { settings ->
            settings[intPreferencesKey(PROP_WORDS_FOR_LOAD_COUNT)] = newWordsCount
        }
    }

    override fun getLoadPhrasesExamples(): Flow<Boolean> {
        return dataStore.data.map {
            preferences -> preferences[booleanPreferencesKey(PROP_LOAD_PHRASES_EXAMPLES)] ?: false
        }
    }

    override suspend fun setLoadPhrasesExamples(isSetLoadPhrases: Boolean) {
        dataStore.edit {  settings ->
            settings[booleanPreferencesKey(PROP_LOAD_PHRASES_EXAMPLES)] = isSetLoadPhrases
        }
    }

    override fun getIsFirstRun(): Flow<Boolean> {
        return dataStore.data.map {
                preferences -> preferences[booleanPreferencesKey(PROP_IS_FIRST_RUN)] ?: true
        }
    }

    override suspend fun setIsFirstRun(isFirstRun: Boolean) {
        dataStore.edit {  settings ->
            settings[booleanPreferencesKey(PROP_IS_FIRST_RUN)] = isFirstRun
        }
    }
}
