package com.github.kutyrev.vocabulator.repository.storage

import com.github.kutyrev.vocabulator.app.di.IoDispatcher
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultStorageRepository @Inject constructor(
    private val vocabulatorDao: VocabulatorDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : StorageRepository {
    override fun getSubtitlesList(): Flow<List<SubtitlesUnit>> =
        vocabulatorDao.getSubtitlesList().flowOn(dispatcher)

    override suspend fun getCards(subtitleId: Int): Flow<List<WordCard>> = withContext(dispatcher) {
        vocabulatorDao.getSubtitlesWordsCards(subtitleId)
    }

    override suspend fun getAllCards(): Flow<List<WordCard>> = withContext(dispatcher) {
        vocabulatorDao.getAllWordsCards()
    }

    override suspend fun getCommonWords(language: Language): List<CommonWord> =
        withContext(dispatcher) {
            vocabulatorDao.getCommonWords(language.ordinal)
        }

    override suspend fun getSubtitlesUnit(id: Int): SubtitlesUnit =
        withContext(dispatcher) {
            vocabulatorDao.getSubtitleUnit(id)
        }

    override suspend fun insertNewSubtitles(subtitlesUnit: SubtitlesUnit): Int =
        withContext(dispatcher) {
            val id = vocabulatorDao.insertSubtitlesInfo(subtitlesUnit).toInt()
            subtitlesUnit.wordCards.forEach { it.subtitleId = id }
            vocabulatorDao.insertWordCards(subtitlesUnit.wordCards)
            return@withContext id
        }

    override suspend fun updateSubtitles(subtitlesUnit: SubtitlesUnit) {
        withContext(dispatcher) {
            vocabulatorDao.updateSubtitlesInfo(subtitlesUnit)
        }
    }

    override suspend fun deleteSubtitles(subtitlesUnit: SubtitlesUnit) {
        withContext(dispatcher) {
            vocabulatorDao.deleteSubtitles(subtitlesUnit)
        }
    }

    override suspend fun insertWordCards(wordCards: List<WordCard>) {
        withContext(dispatcher) {
            vocabulatorDao.insertWordCards(wordCards)
        }
    }

    override suspend fun updateWordCards(wordCards: List<WordCard>) {
        withContext(dispatcher) {
            vocabulatorDao.updateWordCards(wordCards)
        }
    }

    override suspend fun deleteWordCards(wordCards: List<WordCard>) {
        withContext(dispatcher) {
            vocabulatorDao.deleteWordCards(wordCards)
        }
    }

    override suspend fun insertCommonWords(commonWords: List<CommonWord>) {
        withContext(dispatcher) {
            vocabulatorDao.insertCommonWords(commonWords)
        }
    }

    override suspend fun deleteCommonWords(commonWords: List<CommonWord>) {
        withContext(dispatcher) {
            vocabulatorDao.deleteCommonWords(commonWords)
        }
    }
}
