package com.github.kutyrev.vocabulator.repository.translator

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.app.di.IoDispatcher
import com.github.kutyrev.vocabulator.datasource.firebase.CloudBase
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultTranslationRepository @Inject constructor(
    private val fireBaseSource: CloudBase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TranslationRepository {
    override suspend fun getTranslation(
        words: List<WordCard>,
        origLanguage: Language,
        translationLanguage: Language,
        translationCallback: TranslationCallback
    ) {
        withContext(dispatcher) {
            fireBaseSource.getTranslation(
                words,
                origLanguage,
                translationLanguage,
                translationCallback
            )
        }
    }
}
