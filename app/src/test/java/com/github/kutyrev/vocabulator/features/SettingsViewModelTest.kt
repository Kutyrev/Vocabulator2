package com.github.kutyrev.vocabulator.features

import com.github.kutyrev.vocabulator.MainDispatcherRule
import com.github.kutyrev.vocabulator.features.settings.model.SettingsViewModel
import com.github.kutyrev.vocabulator.repository.datastore.SettingsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

const val WORDS_COUNT = 50
const val NEW_WORDS_COUNT = 70

internal class SettingsViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var settingsRepository: SettingsRepository

    lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setUp() {
        settingsViewModel = SettingsViewModel(settingsRepository)
    }

    @Test
    fun startCollectingNumberOfWordsForLoadTest() {
        coEvery {
            settingsRepository.getWordsForLoadCount()
        } returns flowOf(WORDS_COUNT)

        settingsViewModel.startCollectingNumberOfWordsForLoad()
        assertEquals(WORDS_COUNT, settingsViewModel.numberOfWordsForLoad.value)

        coVerify {
            settingsRepository.getWordsForLoadCount()
        }
    }

    @Test
    fun startCollectingLoadPhrasesTest() {
        coEvery {
            settingsRepository.getLoadPhrasesExamples()
        } returns flowOf(true)

        settingsViewModel.startCollectingLoadPhrases()
        assertEquals(true, settingsViewModel.loadPhrasesExamples.value)

        coVerify {
            settingsRepository.getLoadPhrasesExamples()
        }
    }

    @Test
    fun changeNumberOfWordsValue() {
        settingsViewModel.changeNumberOfWordsValue(NEW_WORDS_COUNT)
        assertEquals(NEW_WORDS_COUNT, settingsViewModel.numberOfWordsForLoad.value)
    }

    @Test
    fun changeIsSetLoadPhrasesTest() {
        settingsViewModel.changeIsSetLoadPhrases(true)
        assertEquals(true, settingsViewModel.loadPhrasesExamples.value)
    }

    @Test
    fun onSaveButtonClickTest() {
        coEvery {
            settingsRepository.setWordsForLoadCount(any())
            settingsRepository.setLoadPhrasesExamples(any())
        } just Runs

        settingsViewModel.onSaveButtonClick()

        coVerify {
            settingsRepository.setWordsForLoadCount(any())
            settingsRepository.setLoadPhrasesExamples(any())
        }
    }
}