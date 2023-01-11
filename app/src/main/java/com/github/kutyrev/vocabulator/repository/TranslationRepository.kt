package com.github.kutyrev.vocabulator.repository

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard

interface TranslationRepository {
    suspend fun getTranslation(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language
    )
}
