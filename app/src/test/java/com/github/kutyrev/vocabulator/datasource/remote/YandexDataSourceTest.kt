package com.github.kutyrev.vocabulator.datasource.remote

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

internal class YandexDataSourceTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var yandexApiMock: YandexApi

    private val language = Language.EN

    private val wordsMock = listOf(
        WordCard(1, 1, "mock", ""),
        WordCard(2, 1, "mock2", "")
    )

    @Test
    @ExperimentalCoroutinesApi
    fun translateWordsTestSuccess() = runTest {
        val firstTranslationMock = "mockt"

        val yandexTranslationsMocks =
            listOf(YandexTranslation(firstTranslationMock), YandexTranslation("mockt2"))

        coEvery { yandexApiMock.getTranslation(any(), any()) } returns Response.success(
            YandexResult(yandexTranslationsMocks)
        )

        val yandexDataSource = YandexDataSource(yandexApiMock)

        val result = yandexDataSource.translateWords(wordsMock, language, language)

        assertTrue(result is YandexTranslationResult.Success)
        assertEquals(
            (result as YandexTranslationResult.Success).newTranslatedCards[0].translatedWord,
            firstTranslationMock
        )
    }

    @Test
    @ExperimentalCoroutinesApi
    fun translateWordsTestGenericError() = runTest {
        val errorCode = 400

        coEvery { yandexApiMock.getTranslation(any(), any()) } returns Response.error(
            errorCode,
            "".toResponseBody("".toMediaTypeOrNull())
        )

        val yandexDataSource = YandexDataSource(yandexApiMock)

        val result = yandexDataSource.translateWords(wordsMock, language, language)

        assertTrue(result is YandexTranslationResult.GenericError)
        assertEquals(
            (result as YandexTranslationResult.GenericError).errorCode,
            errorCode
        )
    }
}
