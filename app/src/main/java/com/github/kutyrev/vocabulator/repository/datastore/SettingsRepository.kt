package com.github.kutyrev.vocabulator.repository.datastore

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getWordsForLoadCount(): Flow<Int>
    suspend fun setWordsForLoadCount(newWordsCount: Int)
    fun getLoadPhrasesExamples(): Flow<Boolean>
    suspend fun setLoadPhrasesExamples(isSetLoadPhrases: Boolean)
}
