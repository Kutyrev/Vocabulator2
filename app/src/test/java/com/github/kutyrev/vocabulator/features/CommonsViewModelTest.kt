package com.github.kutyrev.vocabulator.features

import com.github.kutyrev.vocabulator.MainDispatcherRule
import com.github.kutyrev.vocabulator.features.commons.model.CommonsViewModel
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

const val CORRECT_COMMONS_SIZE = 1
const val SEARCH_TEXT = "find"

internal class CommonsViewModelTest {

    private val commonWord = CommonWord(1, 1, "mock")
    private val commonWordsMock = listOf(commonWord)
    private val newLanguage = Language.FR

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var storageRepository: StorageRepository

    private lateinit var commonsViewModel: CommonsViewModel

    @Before
    fun setUp() {
        coEvery {
            storageRepository.getCommonWords(any())
        } returns commonWordsMock

        commonsViewModel = CommonsViewModel(storageRepository)
    }

    @Test
    fun initTest() {
        assertEquals(CORRECT_COMMONS_SIZE, commonsViewModel.words.size)
        assertEquals(commonWord.word, commonsViewModel.words[0].word)
    }

    @Test
    fun onLanguageChangeTest() {
        commonsViewModel.onLanguageChange(newLanguage)
        assertEquals(newLanguage, commonsViewModel.language.value)
    }

    @Test
    fun onSearchTextChangeTest() {
        commonsViewModel.onSearchTextChange(SEARCH_TEXT)
        assertEquals(SEARCH_TEXT, commonsViewModel.searchText.value)
    }

    @Test
    fun onWordCheckedStateChangeTest() {
        commonsViewModel.onWordCheckedStateChange(commonsViewModel.words[0], false)
        assertEquals(false, commonsViewModel.words[0].checked)
        commonsViewModel.onWordCheckedStateChange(commonsViewModel.words[0], true)
        assertEquals(true, commonsViewModel.words[0].checked)
    }

    @Test
    fun onOkButtonPressedTest() {
        coEvery{
            storageRepository.deleteCommonWords(any())
        } just Runs

        commonsViewModel.onWordCheckedStateChange(commonsViewModel.words[0], false)
        commonsViewModel.onOkButtonPressed()

        coVerify {
            storageRepository.deleteCommonWords(any())
        }
    }
}
