package com.github.kutyrev.vocabulator.repository.translator

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.app.di.IoDispatcher
import com.github.kutyrev.vocabulator.datasource.translators.TranslationBackSource
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultTranslationRepository @Inject constructor(
    translationBackSource: TranslationBackSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TranslationRepository {
    override suspend fun getTranslation(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language
    ) {
        withContext(dispatcher) {

        }
    }
}
