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
        val wordsWithoutTranslation: MutableList<String> = mutableListOf()

        words.forEach { if (it.translatedWord.isBlank()) wordsWithoutTranslation.add(it.originalWord) }

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
                val newTranslatedWordsCards: MutableList<WordCard> = mutableListOf()
                resultTranslations?.translations?.forEachIndexed { index, yandexTranslation ->
                    words.find { it.originalWord == wordsWithoutTranslation[index] }?.let {
                        it.translatedWord = yandexTranslation.text
                        newTranslatedWordsCards.add(it)
                    }
                }
                return YandexTranslationResult.Success(newTranslatedWordsCards)
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
