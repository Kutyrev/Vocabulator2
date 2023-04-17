package com.github.kutyrev.vocabulator.repository.storage

import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

internal class DefaultStorageRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var vocabulatorDao: VocabulatorDao

    private val subUnitMock = SubtitlesUnit(1, "Mock", 1, 2)
    private val subtitlesListMock = listOf(subUnitMock)
    private val wordCardsMock = listOf(WordCard(1, 1, "mock", "mock"))
    private val commonWordsMock = listOf(CommonWord(1, 1, "mock"))
    private val subtitlesId = 1
    private val language = Language.EN

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getSubtitlesListTest() = runTest {
        coEvery {
            vocabulatorDao.getSubtitlesList()
        } returns flowOf(subtitlesListMock)

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        val subtitlesList = defaultStorageRepository.getSubtitlesList().first()

        assertTrue(subtitlesList == subtitlesListMock)

        coVerify {
            vocabulatorDao.getSubtitlesList()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCardsTest() = runTest {
        coEvery {
            vocabulatorDao.getSubtitlesWordsCards(any())
        } returns flowOf(wordCardsMock)

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        val wordsList = defaultStorageRepository.getCards(subtitlesId).first()

        assertTrue(wordsList == wordCardsMock)

        coVerify {
            vocabulatorDao.getSubtitlesWordsCards(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllCardsTest() = runTest {
        coEvery {
            vocabulatorDao.getAllWordsCards()
        } returns flowOf(wordCardsMock)

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        val wordsList = defaultStorageRepository.getAllCards().first()

        assertTrue(wordsList == wordCardsMock)

        coVerify {
            vocabulatorDao.getAllWordsCards()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCommonWordsTest() = runTest {
        coEvery {
            vocabulatorDao.getCommonWords(any())
        } returns commonWordsMock

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        val wordsList = defaultStorageRepository.getCommonWords(language)

        assertTrue(wordsList == commonWordsMock)

        coVerify {
            vocabulatorDao.getCommonWords(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getSubtitlesUnitTest() = runTest {
        coEvery {
            vocabulatorDao.getSubtitleUnit(any())
        } returns subUnitMock

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        val subUnit = defaultStorageRepository.getSubtitlesUnit(subtitlesId)

        assertTrue(subUnit == subUnitMock)

        coVerify {
            vocabulatorDao.getSubtitleUnit(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertNewSubtitlesTest() = runTest {
        coEvery {
            vocabulatorDao.insertSubtitlesInfo(any())
        } returns subtitlesId.toLong()
        coEvery {
            vocabulatorDao.insertWordCards(any())
        } just Runs

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)
        subUnitMock.wordCards.addAll(wordCardsMock)

        val subNewId = defaultStorageRepository.insertNewSubtitles(subUnitMock)

        assertTrue(subtitlesId == subNewId)

        coVerify {
            vocabulatorDao.insertSubtitlesInfo(subUnitMock)
            vocabulatorDao.insertWordCards(wordCardsMock)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateSubtitles() = runTest {
        coEvery {
            vocabulatorDao.updateSubtitlesInfo(any())
        } just Runs

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        defaultStorageRepository.updateSubtitles(subUnitMock)

        coVerify {
            vocabulatorDao.updateSubtitlesInfo(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteSubtitles() = runTest {
        coEvery {
            vocabulatorDao.deleteSubtitles(any())
        } just Runs

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        defaultStorageRepository.deleteSubtitles(subUnitMock)

        coVerify {
            vocabulatorDao.deleteSubtitles(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertWordCards() = runTest {
        coEvery {
            vocabulatorDao.insertWordCards(any())
        } just Runs

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        defaultStorageRepository.insertWordCards(wordCardsMock)

        coVerify {
            vocabulatorDao.insertWordCards(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateWordCards() = runTest {
        coEvery {
            vocabulatorDao.updateWordCards(any())
        } just Runs

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        defaultStorageRepository.updateWordCards(wordCardsMock)

        coVerify {
            vocabulatorDao.updateWordCards(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteWordCards() = runTest {
        coEvery {
            vocabulatorDao.deleteWordCards(any())
        } just Runs

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        defaultStorageRepository.deleteWordCards(wordCardsMock)

        coVerify {
            vocabulatorDao.deleteWordCards(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertCommonWords() = runTest {
        coEvery {
            vocabulatorDao.insertCommonWords(any())
        } just Runs

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        defaultStorageRepository.insertCommonWords(commonWordsMock)

        coVerify {
            vocabulatorDao.insertCommonWords(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteCommonWords() = runTest {
        coEvery {
            vocabulatorDao.deleteCommonWords(commonWordsMock)
        } just Runs

        val defaultStorageRepository = DefaultStorageRepository(vocabulatorDao)

        defaultStorageRepository.deleteCommonWords(commonWordsMock)

        coVerify {
            vocabulatorDao.deleteCommonWords(any())
        }
    }
}