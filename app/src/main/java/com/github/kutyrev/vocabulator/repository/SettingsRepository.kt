package com.github.kutyrev.vocabulator.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getWordsForLoadCount(): Flow<Int>
    suspend fun setWordsForLoadCount(newWordsCount: Int)
}
