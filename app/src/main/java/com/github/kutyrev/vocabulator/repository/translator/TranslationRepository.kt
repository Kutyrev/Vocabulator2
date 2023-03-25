package com.github.kutyrev.vocabulator.repository.translator

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard

interface TranslationRepository {
    suspend fun getForebaseTranslation(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language,
        translationCallback: TranslationCallback
    )
    suspend fun getYandexTranslation(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language,
        translationCallback: TranslationCallback
    )
}
