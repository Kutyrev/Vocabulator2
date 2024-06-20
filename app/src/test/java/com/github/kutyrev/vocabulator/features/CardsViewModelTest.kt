package com.github.kutyrev.vocabulator.features

import androidx.lifecycle.SavedStateHandle
import com.github.kutyrev.vocabulator.MainDispatcherRule
import com.github.kutyrev.vocabulator.features.cards.model.CardsMessages
import com.github.kutyrev.vocabulator.features.cards.model.CardsViewModel
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val LIST_ID = "1"
private const val EMPTY_LIST_ID = "-1"
private const val POSITIVE_OFFSET: Float = 1f
private const val NEGATIVE_OFFSET: Float = -1f

internal class CardsViewModelTest {

    private val mockWordCardOne = WordCard(1, 1, "mock", "mock")
    private val mockWordCardTwo = WordCard(2, 1, "mock2", "mock2")
    private val mockWordCards = listOf(mockWordCardOne, mockWordCardTwo)
    private val subUnitMock = SubtitlesUnit(1, "Mock", 1, 2)
    private val newTranslation = "changed mock"

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var storageRepository: StorageRepository

    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    private lateinit var cardsViewModel: CardsViewModel

    @Before
    fun setUp() {
        coEvery {
            savedStateHandle.get<String>(any())
        } returns LIST_ID
        coEvery {
            storageRepository.getCards(any())
        } returns flowOf(mockWordCards)

        cardsViewModel = CardsViewModel(savedStateHandle, storageRepository)
    }

    @Test
    fun initTestWithListIdTest() {
        assertEquals(LIST_ID.toInt(), cardsViewModel.listId.value)
        assertEquals(mockWordCards, cardsViewModel.cards.value)
        assertEquals(mockWordCardOne, cardsViewModel.card.value)

        coVerify {
            savedStateHandle.get<String>(any())
            storageRepository.getCards(LIST_ID.toInt())
        }
    }

    @Test
    fun initTestWithoutListIdTest() {
        coEvery {
            savedStateHandle.get<String>(any())
        } returns EMPTY_LIST_ID
        coEvery {
            storageRepository.getAllCards()
        } returns flowOf(mockWordCards)

        val cardsViewModelEmptyList = CardsViewModel(savedStateHandle, storageRepository)
        assertEquals(EMPTY_LIST_ID.toInt(), cardsViewModelEmptyList.listId.value)
        assertEquals(mockWordCards, cardsViewModelEmptyList.cards.value)
        assertEquals(mockWordCardOne, cardsViewModelEmptyList.card.value)

        coVerify {
            savedStateHandle.get<String>(any())
            storageRepository.getAllCards()
        }
    }

    @Test
    fun emitNewCardTest() {
        val cardsViewModel = CardsViewModel(savedStateHandle, storageRepository)
        assertEquals(mockWordCardOne, cardsViewModel.card.value)
        cardsViewModel.emitNewCard(NEGATIVE_OFFSET)
        assertEquals(mockWordCardTwo, cardsViewModel.card.value)
        cardsViewModel.emitNewCard(POSITIVE_OFFSET)
        assertEquals(mockWordCardOne, cardsViewModel.card.value)
    }

    @Test
    fun onChangeIsRandomCardsStateTest() {
        assertEquals(false, cardsViewModel.isRandomCards.value)
        cardsViewModel.onChangeIsRandomCardsState(true)
        assertEquals(true, cardsViewModel.isRandomCards.value)
    }

    @Test
    fun onNextPreviousCardButtonPressedTest() {
        val cardsViewModel = CardsViewModel(savedStateHandle, storageRepository)
        assertEquals(mockWordCardOne, cardsViewModel.card.value)
        cardsViewModel.onNextCardButtonPressed()
        assertEquals(mockWordCardTwo, cardsViewModel.card.value)
        cardsViewModel.onPreviousCardButtonPressed()
        assertEquals(mockWordCardOne, cardsViewModel.card.value)
    }

    @Test
    fun deleteWordCardTest() {
        coEvery {
            storageRepository.deleteWordCards(any())
        } just Runs

        cardsViewModel.deleteWordCard()

        assertEquals(CardsMessages.DELETED.ordinal, cardsViewModel.messages.value?.ordinal)
        assertEquals(mockWordCardTwo, cardsViewModel.card.value)

        coVerify {
            storageRepository.deleteWordCards(listOf(mockWordCardOne))
        }
    }

    @Test
    fun addWordInCommonsTest() {
        coEvery {
            storageRepository.getSubtitlesUnit(any())
        } returns subUnitMock
        coEvery {
            storageRepository.insertCommonWords(any())
        } just Runs
        coEvery {
            storageRepository.deleteWordCards(any())
        } just Runs

        cardsViewModel.addWordInCommons()
        assertEquals(CardsMessages.ADDEDINCOMMONS.ordinal, cardsViewModel.messages.value?.ordinal)
        assertEquals(mockWordCardTwo, cardsViewModel.card.value)

        coVerify {
            storageRepository.getSubtitlesUnit(mockWordCardOne.subtitleId)
            storageRepository.insertCommonWords(any())
            storageRepository.deleteWordCards(listOf(mockWordCardOne))
        }
    }

    @Test
    fun onIsEditStateChangeTest() {
        assertEquals(false, cardsViewModel.isEdit.value)
        cardsViewModel.onIsEditStateChange(true)
        assertEquals(true, cardsViewModel.isEdit.value)
    }

    @Test
    fun onChangeTranslationTest() {
        coEvery {
            storageRepository.updateWordCards(listOf(mockWordCardOne))
        } just Runs

        cardsViewModel.onChangeTranslation(newTranslation)
        assertEquals(CardsMessages.TRANSLATIONEDITED.ordinal, cardsViewModel.messages.value?.ordinal)
        assertEquals(newTranslation, cardsViewModel.card.value.translatedWord)

        coVerify {
            storageRepository.updateWordCards(listOf(mockWordCardOne))
        }
    }

    @Test
    fun onCardClickTest() {
        assertEquals(false, cardsViewModel.showTranslation.value)
        cardsViewModel.onCardClick()
        assertEquals(true, cardsViewModel.showTranslation.value)
    }
}
