package com.github.kutyrev.vocabulator.datasource.translators

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard

class VocabulatorBackSource : TranslationBackSource {
    override suspend fun getTranslation(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language
    ) {
        TODO("Not yet implemented")
    }
}
