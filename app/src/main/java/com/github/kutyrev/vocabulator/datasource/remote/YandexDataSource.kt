package com.github.kutyrev.vocabulator.datasource.remote

import com.github.kutyrev.vocabulator.BuildConfig
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard
import java.util.*
import javax.inject.Inject

class YandexDataSource @Inject constructor(
    private val yandexApi: YandexApi,
) : YandexSource {
    override suspend fun translateWords(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language
    ): YandexTranslationResult {
        val wordsWithoutTranslation =
            words.filter { it.translatedWord.isEmpty() }.map { it.originalWord }

        val yandexTranslateResult = kotlin.runCatching {
            yandexApi.getTranslation(
                BuildConfig.YANDEX_AUTH_KEY,
                YandexRequestParams(
                    sourceLanguageCode = origLanguage.name.lowercase(Locale.getDefault()),
                    targetLanguageCode = translationLanguage.name.lowercase(Locale.getDefault()),
                    texts = wordsWithoutTranslation
                )
            )
        }.getOrNull()

        when {
            yandexTranslateResult != null && yandexTranslateResult.isSuccessful -> {
                val resultTranslations = yandexTranslateResult.body()
                resultTranslations?.translations?.forEachIndexed { index, yandexTranslation ->
                    words.find { it.originalWord == wordsWithoutTranslation[index] }?.translatedWord =
                        yandexTranslation.text
                }
                return YandexTranslationResult.Success(words)
            }
            yandexTranslateResult != null && !yandexTranslateResult.isSuccessful -> {
                return YandexTranslationResult.GenericError(yandexTranslateResult.code())
            }
            else -> {
                return YandexTranslationResult.NetworkError
            }
        }
    }
}
