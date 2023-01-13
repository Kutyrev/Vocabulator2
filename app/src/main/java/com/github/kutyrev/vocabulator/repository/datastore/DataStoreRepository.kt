package com.github.kutyrev.vocabulator.repository.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PROP_WORDS_FOR_LOAD_COUNT = "words_for_load_count"

class DataStoreRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    SettingsRepository {

    override fun getWordsForLoadCount(): Flow<Int> {
        return dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[intPreferencesKey(PROP_WORDS_FOR_LOAD_COUNT)] ?: 100
            }
    }

    override suspend fun setWordsForLoadCount(newWordsCount: Int) {
        dataStore.edit { settings ->
            settings[intPreferencesKey(PROP_WORDS_FOR_LOAD_COUNT)] = newWordsCount
        }
    }
}
