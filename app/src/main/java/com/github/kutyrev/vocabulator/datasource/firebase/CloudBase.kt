package com.github.kutyrev.vocabulator.datasource.firebase

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard

interface CloudBase {
    suspend fun getTranslation(
        wordsToTranslate: List<WordCard>,
        origLanguage: Language,
        transLanguage: Language
    )
}
