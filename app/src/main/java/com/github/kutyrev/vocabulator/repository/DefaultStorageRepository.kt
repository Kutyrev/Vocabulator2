package com.github.kutyrev.vocabulator.repository

import com.github.kutyrev.vocabulator.app.di.IoDispatcher
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultStorageRepository @Inject constructor(
    private val vocabulatorDao: VocabulatorDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : StorageRepository {
    override suspend fun getSubtitlesList(): Flow<List<SubtitlesUnit>> = withContext(dispatcher) {
        vocabulatorDao.getSubtitlesList()
    }

    override suspend fun getCards(subtitleId: Int): Flow<List<WordCard>> = withContext(dispatcher) {
        vocabulatorDao.getSubtitlesWordsCards(subtitleId)
    }

    override suspend fun getCommonWords(language: Language): List<CommonWord> =
        withContext(dispatcher) {
            vocabulatorDao.getCommonWords(language.ordinal)
        }

    override suspend fun insertNewSubtitles(subtitlesUnit: SubtitlesUnit): Int =
        withContext(dispatcher) {
            val id = vocabulatorDao.insertSubtitlesInfo(subtitlesUnit).toInt()
            subtitlesUnit.wordCards.forEach { it.subtitleId = id }
            vocabulatorDao.insertWordCards(subtitlesUnit.wordCards)
            return@withContext id
        }
}
