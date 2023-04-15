package com.github.kutyrev.vocabulator.repository.translator

import com.github.kutyrev.vocabulator.datasource.firebase.CloudBase
import com.github.kutyrev.vocabulator.datasource.remote.YandexSource
import com.github.kutyrev.vocabulator.datasource.remote.YandexTranslationResult
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

private const val GENERIC_ERROR_CODE = 401

internal class DefaultTranslationRepositoryTest : TranslationCallback {

    private val mockTranslationWords = listOf(WordCard(1, 1, "mock", "mock"))
    private val origLang = Language.EN
    private val transLang = Language.IT

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var yandexDataSource: YandexSource

    @MockK
    lateinit var firebaseSource: CloudBase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getFirebaseTranslationTest() = runTest {
        coEvery {
            firebaseSource.getTranslation(
                any(),
                any(),
                any(),
                any()
            )
        } just Runs

        val translationRepository = DefaultTranslationRepository(firebaseSource, yandexDataSource)

        translationRepository.getFirebaseTranslation(
            mockTranslationWords,
            origLang,
            transLang,
            this@DefaultTranslationRepositoryTest
        )

        coVerify {
            firebaseSource.getTranslation(
                mockTranslationWords,
                origLang,
                transLang,
                this@DefaultTranslationRepositoryTest
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getYandexTranslationTestSuccess() = runTest {
        coEvery {
            yandexDataSource.translateWords(
                any(),
                any(),
                any()
            )
        } returns YandexTranslationResult.Success(mockTranslationWords)
        coEvery { firebaseSource.saveNewWords(any(), any(), any()) } just Runs

        val translationRepository = DefaultTranslationRepository(firebaseSource, yandexDataSource)

        translationRepository.getYandexTranslation(
            mockTranslationWords,
            origLang,
            transLang,
            this@DefaultTranslationRepositoryTest
        )

        coVerify {
            yandexDataSource.translateWords(mockTranslationWords, origLang, transLang)
            firebaseSource.saveNewWords(mockTranslationWords, origLang, transLang)
            this@DefaultTranslationRepositoryTest.receiveTranslation(
                mockTranslationWords,
                TranslationResultStatus.Success
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getYandexTranslationTestGenericError() = runTest {
        coEvery {
            yandexDataSource.translateWords(
                any(),
                any(),
                any()
            )
        } returns YandexTranslationResult.GenericError(GENERIC_ERROR_CODE)

        val translationRepository = DefaultTranslationRepository(firebaseSource, yandexDataSource)

        translationRepository.getYandexTranslation(
            mockTranslationWords,
            origLang,
            transLang,
            this@DefaultTranslationRepositoryTest
        )

        coVerify {
            yandexDataSource.translateWords(mockTranslationWords, origLang, transLang)
            this@DefaultTranslationRepositoryTest.receiveTranslation(
                mockTranslationWords,
                TranslationResultStatus.YandexGenericError
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getYandexTranslationTestNetworkError() = runTest {
        coEvery {
            yandexDataSource.translateWords(
                any(),
                any(),
                any()
            )
        } returns YandexTranslationResult.NetworkError

        val translationRepository = DefaultTranslationRepository(firebaseSource, yandexDataSource)

        translationRepository.getYandexTranslation(
            mockTranslationWords,
            origLang,
            transLang,
            this@DefaultTranslationRepositoryTest
        )

        coVerify {
            yandexDataSource.translateWords(mockTranslationWords, origLang, transLang)
            this@DefaultTranslationRepositoryTest.receiveTranslation(
                mockTranslationWords,
                TranslationResultStatus.YandexNetworkError
            )
        }
    }

    override fun receiveTranslation(
        translatedWords: List<WordCard>,
        translationResult: TranslationResultStatus
    ) {
    }
}
