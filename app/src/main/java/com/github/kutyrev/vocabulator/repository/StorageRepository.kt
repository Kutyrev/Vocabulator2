package com.github.kutyrev.vocabulator.repository

import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    suspend fun getSubtitlesList(): Flow<List<SubtitlesUnit>>
    suspend fun getCards(subtitleId: Int): Flow<List<WordCard>>
    suspend fun getCommonWords(language: Language): List<CommonWord>
    suspend fun getSubtitlesUnit(id: Int): SubtitlesUnit
    suspend fun insertNewSubtitles(subtitlesUnit: SubtitlesUnit): Int
}
