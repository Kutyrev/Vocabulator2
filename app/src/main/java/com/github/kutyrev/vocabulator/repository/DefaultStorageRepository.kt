package com.github.kutyrev.vocabulator.repository

import com.github.kutyrev.vocabulator.app.di.IoDispatcher
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultStorageRepository @Inject constructor(
    private val vocabulatorDao: VocabulatorDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    StorageRepository {
    override suspend fun getSubtitlesList(): Flow<List<SubtitlesUnit>> = withContext(dispatcher) {
        flowOf(listOf(SubtitlesUnit(1, "Test", 1, 2)))
    }

    override suspend fun getCards(subtitleId: Int): Flow<List<WordCard>> = withContext(dispatcher) {
        flowOf(
            listOf(
                WordCard(
                    1,
                    1,
                    "to binge",
                    "позволять себе"),
                WordCard(
                    2,
                    1,
                    "to do",
                    "делать"
                )
            )
        )
    }
}
