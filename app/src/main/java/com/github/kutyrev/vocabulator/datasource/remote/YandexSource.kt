package com.github.kutyrev.vocabulator.datasource.remote

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard

interface YandexSource {
    suspend fun translateWords(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language
    ): YandexTranslationResult
}
