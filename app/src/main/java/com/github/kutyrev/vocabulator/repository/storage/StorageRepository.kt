package com.github.kutyrev.vocabulator.repository.storage

import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    suspend fun getSubtitlesList(): Flow<List<SubtitlesUnit>>
    suspend fun getSubtitlesUnit(id: Int): SubtitlesUnit
    suspend fun insertNewSubtitles(subtitlesUnit: SubtitlesUnit): Int
    suspend fun updateSubtitles(subtitlesUnit: SubtitlesUnit)
    suspend fun deleteSubtitles(subtitlesUnit: SubtitlesUnit)

    suspend fun getCards(subtitleId: Int): Flow<List<WordCard>>
    suspend fun getAllCards(): Flow<List<WordCard>>
    suspend fun insertWordCards(wordCards: List<WordCard>)
    suspend fun updateWordCards(wordCards: List<WordCard>)
    suspend fun deleteWordCards(wordCards: List<WordCard>)

    suspend fun getCommonWords(language: Language): List<CommonWord>
    suspend fun insertCommonWords(commonWords: List<CommonWord>)
    suspend fun deleteCommonWords(commonWords: List<CommonWord>)
}
