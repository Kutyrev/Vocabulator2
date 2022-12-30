package com.github.kutyrev.vocabulator.repository

import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    suspend fun getSubtitlesList(): Flow<List<SubtitlesUnit>>
}
