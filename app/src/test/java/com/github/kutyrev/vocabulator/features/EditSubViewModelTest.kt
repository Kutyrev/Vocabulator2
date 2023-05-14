package com.github.kutyrev.vocabulator.features

import androidx.lifecycle.SavedStateHandle
import com.github.kutyrev.vocabulator.MainDispatcherRule
import com.github.kutyrev.vocabulator.features.editsub.model.EditSubViewModel
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.repository.file.FileRepository
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import com.github.kutyrev.vocabulator.repository.translator.TranslationRepository
import com.github.kutyrev.vocabulator.repository.translator.TranslationResultStatus
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

private const val LIST_ID = "1"
private const val CORRECT_WORDS_COUNT = 2
private const val CHANGED_WORD = "changed mock"

internal class EditSubViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val subUnitMock = SubtitlesUnit(1, "Mock", 1, 2)
    private val mockWordCardOne = WordCard(1, 1, "mock", "mock")
    private val mockWordCardTwo = WordCard(2, 1, "mock2", "mock2")
    private val mockWordCardThree = WordCard(1, 1, "mock3", "mock3")
    private val mockWordCardFour = WordCard(2, 1, "mock4", "mock4")
    private val mockWordCards = listOf(mockWordCardOne, mockWordCardTwo)
    private val mockWordCardsTwo = listOf(mockWordCardThree, mockWordCardFour)
    private val subsLanguage = Language.FR
    private val newSubsLanguage = Language.IT

    @MockK
    lateinit var storageRepository: StorageRepository

    @MockK
    lateinit var fileRepository: FileRepository

    @MockK
    lateinit var translationRepository: TranslationRepository

    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    private lateinit var editSubViewModel: EditSubViewModel

    @Before
    fun setUp() {
        coEvery {
            savedStateHandle.get<String>(any())
        } returns LIST_ID
        coEvery {
            storageRepository.getSubtitlesUnit(any())
        } returns subUnitMock
        coEvery {
            storageRepository.getCards(any())
        } returns flowOf(mockWordCards)
        coEvery {
            fileRepository.sortedWords
        } returns mapOf()

        editSubViewModel = EditSubViewModel(
            savedStateHandle = savedStateHandle,
            storageRepository = storageRepository,
            translationRepository = translationRepository,
            fileRepository = fileRepository
        )
    }

    @Test
    fun initTest() {
        assertEquals(subUnitMock, editSubViewModel.subtitlesUnit.value)
        assertEquals(subsLanguage, editSubViewModel.subsLanguage.value)
        assertEquals(subUnitMock.name, editSubViewModel.subtitlesName.value)
        assertEquals(
            mockWordCardOne.originalWord,
            editSubViewModel.subtitlesUnit.value?.wordCards?.get(0)?.originalWord
        )
        assertEquals(CORRECT_WORDS_COUNT, editSubViewModel.subtitlesUnit.value?.wordCards?.size)
        assertEquals(CORRECT_WORDS_COUNT, editSubViewModel.words.size)
        assertEquals(mockWordCardOne.originalWord, editSubViewModel.words[0].originalWord)
    }

    @Test
    fun onOrigWordChangeTest() {
        editSubViewModel.onOrigWordChange(CHANGED_WORD, editSubViewModel.words[0])
        assertEquals(CHANGED_WORD, editSubViewModel.words[0].originalWord)
    }

    @Test
    fun onTranslationChangeTest() {
        editSubViewModel.onTranslationChange(CHANGED_WORD, editSubViewModel.words[0])
        assertEquals(CHANGED_WORD, editSubViewModel.words[0].translatedWord)
    }

    @Test
    fun translateWordsTest() {
        coEvery {
            translationRepository.getFirebaseTranslation(any(), any(), any(), any())
        } just Runs

        editSubViewModel.translateWords()
        coVerify {
            translationRepository.getFirebaseTranslation(any(), any(), any(), any())
        }
    }

    @Test
    fun onSubtitleNameChangeTest() {
        editSubViewModel.onSubtitleNameChange(CHANGED_WORD)
        assertEquals(CHANGED_WORD, editSubViewModel.subtitlesName.value)
    }

    @Test
    fun onSubtitlesLanguageChangeTest() {
        editSubViewModel.onSubtitlesLanguageChange(newSubsLanguage)
        assertEquals(newSubsLanguage, editSubViewModel.subsLanguage.value)
    }

    @Test
    fun onTargetLanguageChangeTest() {
        editSubViewModel.onTargetLanguageChange(newSubsLanguage)
        assertEquals(newSubsLanguage, editSubViewModel.langOfTranslation.value)
    }

    @Test
    fun onWordCheckedStateChangeTest() {
        editSubViewModel.onWordCheckedStateChange(editSubViewModel.words[0], false)
        assertEquals(false, editSubViewModel.words[0].checked)
        editSubViewModel.onWordCheckedStateChange(editSubViewModel.words[0], true)
        assertEquals(true, editSubViewModel.words[0].checked)
    }

    @Test
    fun onChangeUncheckedToDictTest() {
        editSubViewModel.onChangeUncheckedToDict(false)
        assertEquals(false, editSubViewModel.uncheckedToDict.value)
        editSubViewModel.onChangeUncheckedToDict(true)
        assertEquals(true, editSubViewModel.uncheckedToDict.value)
    }

    @Test
    fun onOkButtonPressedTest() {
        coEvery {
            storageRepository.updateSubtitles(any())
            storageRepository.deleteWordCards(any())
            storageRepository.updateWordCards(any())
            storageRepository.insertCommonWords(any())
        } just Runs
        editSubViewModel.onSubtitleNameChange(CHANGED_WORD)
        editSubViewModel.onTargetLanguageChange(newSubsLanguage)
        editSubViewModel.onChangeUncheckedToDict(true)
        editSubViewModel.onWordCheckedStateChange(editSubViewModel.words[0], false)
        editSubViewModel.onTranslationChange(CHANGED_WORD, editSubViewModel.words[1])

        editSubViewModel.onOkButtonPressed()

        coVerify {
            storageRepository.updateSubtitles(any())
            storageRepository.deleteWordCards(any())
            storageRepository.updateWordCards(any())
            storageRepository.insertCommonWords(any())
        }
    }

    @Test
    fun receiveTranslationTest() {
        coEvery {
            translationRepository.getYandexTranslation(any(), any(), any(), any())
        } just Runs

        editSubViewModel.receiveTranslation(listOf(), TranslationResultStatus.Success)
        assertEquals(EditSubViewModel.EditCardsMessages.SUCCESS, editSubViewModel.messages.value)

        editSubViewModel.receiveTranslation(listOf(), TranslationResultStatus.YandexGenericError)
        assertEquals(
            EditSubViewModel.EditCardsMessages.YANDEX_GENERIC_ERROR,
            editSubViewModel.messages.value
        )

        editSubViewModel.receiveTranslation(listOf(), TranslationResultStatus.Success)
        assertEquals(EditSubViewModel.EditCardsMessages.SUCCESS, editSubViewModel.messages.value)

        editSubViewModel.receiveTranslation(listOf(), TranslationResultStatus.YandexNetworkError)
        assertEquals(
            EditSubViewModel.EditCardsMessages.NETWORK_ERROR,
            editSubViewModel.messages.value
        )

        editSubViewModel.receiveTranslation(listOf(), TranslationResultStatus.FirebaseError)
        assertEquals(
            EditSubViewModel.EditCardsMessages.FIREBASE_ERROR,
            editSubViewModel.messages.value
        )

        editSubViewModel.receiveTranslation(listOf(), TranslationResultStatus.FirebaseSuccess)

        coVerify {
            translationRepository.getYandexTranslation(
                editSubViewModel.words,
                editSubViewModel.subsLanguage.value,
                editSubViewModel.langOfTranslation.value,
                any()
            )
        }
    }

    @Test
    fun onTranslationClickTest() {
        editSubViewModel.onTranslationClick(editSubViewModel.words[0])
        assertEquals(
            editSubViewModel.words[0].translatedWord,
            editSubViewModel.checkableWords[0].word
        )
        assertEquals(true, editSubViewModel.isEdit.value)
    }

    @Test
    fun onIsEditStateChangeTest() {
        editSubViewModel.onIsEditStateChange(true)
        assertEquals(true, editSubViewModel.isEdit.value)
    }

    @Test
    fun onChangeTranslationTest() {
        editSubViewModel.onTranslationClick(editSubViewModel.words[0])
        editSubViewModel.onChangeTranslation(CHANGED_WORD)
        assertEquals(CHANGED_WORD, editSubViewModel.words[0].translatedWord)
        assertEquals(true, editSubViewModel.words[0].changed)
        assertEquals(false, editSubViewModel.isEdit.value)
    }

    @Test
    fun updateCommonsAndReloadFileTest() {
        coEvery {
            fileRepository.reparseSubtitles(any())
        } returns mockWordCardsTwo

        coEvery {
            storageRepository.deleteWordCards(any())
            storageRepository.insertCommonWords(any())
        } just Runs

        coEvery {
            storageRepository.insertWordCards(any())
        } just Runs

        editSubViewModel.onChangeUncheckedToDict(true)
        editSubViewModel.onWordCheckedStateChange(editSubViewModel.words[0], false)
        editSubViewModel.updateCommonsAndReloadFile()

        coVerify {
            storageRepository.insertCommonWords(any())
            fileRepository.reparseSubtitles(subUnitMock)
            storageRepository.deleteWordCards(mockWordCards)
            storageRepository.insertWordCards(mockWordCardsTwo)
        }
    }

    @Test
    fun setAddNewWordCardDialogVisibilityAndAddNewWordCardTest() {
        coEvery {
            storageRepository.insertWordCards(any())
        } just Runs

        editSubViewModel.setAddNewWordCardDialogVisibility(true)
        assertEquals(true, editSubViewModel.showAddNewWordCardDialog.value)

        editSubViewModel.addNewWordCard(CHANGED_WORD, CHANGED_WORD)

        assertEquals(false, editSubViewModel.showAddNewWordCardDialog.value)
        assertEquals(
            EditSubViewModel.EditCardsMessages.NEW_WORD_ADDED,
            editSubViewModel.messages.value
        )

        coVerify {
            storageRepository.insertWordCards(any())
        }
    }
}
