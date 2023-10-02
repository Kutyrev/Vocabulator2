package com.github.kutyrev.vocabulator.features

import android.net.Uri
import com.github.kutyrev.vocabulator.MainDispatcherRule
import com.github.kutyrev.vocabulator.features.mainlist.model.MainListViewModel
import com.github.kutyrev.vocabulator.model.EMPTY_SUBS_ID
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordsCount
import com.github.kutyrev.vocabulator.repository.datastore.SettingsRepository
import com.github.kutyrev.vocabulator.repository.file.FileLoadStatus
import com.github.kutyrev.vocabulator.repository.file.FileRepository
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val CORRECT_FILENAME = "test.srt"
private const val INCORRECT_FILENAME = "test.pdf"
private const val NEW_SUBS_ID = 4

internal class MainListViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val subUnitMock = SubtitlesUnit(1, "Mock", 1, 2)
    private val subtitlesList = listOf(subUnitMock)
    private val subsLanguage = Language.FR
    private val subWordsCount = WordsCount(1, 99)
    private val wordsCountList = listOf(subWordsCount)

    @MockK
    lateinit var storageRepository: StorageRepository
    @MockK
    lateinit var fileRepository: FileRepository
    @MockK
    lateinit var settingsRepository: SettingsRepository

    private lateinit var mainListViewModel: MainListViewModel

    @Before
    fun setUp() {
        coEvery {
            storageRepository.getSubtitlesList()
        } returns flowOf(subtitlesList)

        coEvery {
            storageRepository.getWordsCount()
        } returns flowOf(wordsCountList)

        mainListViewModel = MainListViewModel(
            storageRepository = storageRepository,
            fileRepository = fileRepository,
            settingsRepository = settingsRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initTest() = runTest {
        assertEquals(subtitlesList, mainListViewModel.subtitlesList.first())
        coVerify {
            storageRepository.getSubtitlesList()
        }
    }

    @Test
    fun checkFileExtensionTest() {
        assertTrue(mainListViewModel.checkFileExtension(CORRECT_FILENAME))
        assertFalse(mainListViewModel.checkFileExtension(INCORRECT_FILENAME))
    }

    @Test
    fun parseFileTest() {
        coEvery {
            fileRepository.parseFile(any(), any(), any())
        } returns FileLoadStatus.FileLoaded(subUnitMock)

        coEvery {
            storageRepository.insertNewSubtitles(any())
        } returns NEW_SUBS_ID

        mainListViewModel.setSubsLanguage(subsLanguage)
        mainListViewModel.parseFile(mockk<Uri>() , CORRECT_FILENAME)

        assertEquals(NEW_SUBS_ID, mainListViewModel.newSubsId.value)
        assertTrue(mainListViewModel.fileLoadingStatus.value is FileLoadStatus.FileLoaded)

        coVerify {
            fileRepository.parseFile(any(), subsLanguage, CORRECT_FILENAME)
            storageRepository.insertNewSubtitles(subUnitMock)
        }
    }

    @Test
    fun onSubtitleSwipedTest() {
        coEvery {
            storageRepository.deleteSubtitles(any())
        } just Runs

        mainListViewModel.onSubtitleSwiped(subUnitMock)

        coVerify {
            storageRepository.deleteSubtitles(subUnitMock)
        }
    }

    @Test
    fun setUnswipedSubtitleUnitTest() {
        mainListViewModel.setUnswipedSubtitleUnit(subUnitMock)
        assertEquals(subUnitMock, mainListViewModel.unswipedSubtitlesUnit.value)
    }

    @Test
    fun resetLoadingStatusTest() {
        coEvery {
            fileRepository.parseFile(any(), any(), any())
        } returns FileLoadStatus.FileLoaded(subUnitMock)

        coEvery {
            storageRepository.insertNewSubtitles(any())
        } returns NEW_SUBS_ID

        mainListViewModel.setSubsLanguage(subsLanguage)
        mainListViewModel.parseFile(mockk<Uri>() , CORRECT_FILENAME)

        mainListViewModel.resetLoadingStatus()
        assertEquals(EMPTY_SUBS_ID, mainListViewModel.newSubsId.value)
        assertTrue(mainListViewModel.fileLoadingStatus.value is FileLoadStatus.None)
    }

    @Test
    fun checkIsFirstRunResetShowTutorialStatusTest()  {
        coEvery {
            settingsRepository.getIsFirstRun()
        } returns flowOf(true)
        coEvery {
            settingsRepository.setIsFirstRun(any())
        } just Runs

        mainListViewModel.checkIsFirstRun()

        assertEquals(true, mainListViewModel.showTutorial.value)

        mainListViewModel.resetShowTutorialStatus()

        assertEquals(false, mainListViewModel.showTutorial.value)

        coVerify {
            settingsRepository.getIsFirstRun()
            settingsRepository.setIsFirstRun(false)
        }
    }
}
