package com.github.kutyrev.vocabulator.repository.translator

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.app.di.IoDispatcher
import com.github.kutyrev.vocabulator.datasource.firebase.CloudBase
import com.github.kutyrev.vocabulator.datasource.remote.YandexSource
import com.github.kutyrev.vocabulator.datasource.remote.YandexTranslationResult
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultTranslationRepository @Inject constructor(
    private val fireBaseSource: CloudBase,
    private val yandexDataSource: YandexSource,
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
                translationLanguage
            )
            val yandexTranslationResult = yandexDataSource.translateWords(
                words, origLanguage,
                translationLanguage
            )

            val translationResult = when(yandexTranslationResult){
                is YandexTranslationResult.GenericError -> TranslationResultStatus.YandexGenericError
                is YandexTranslationResult.NetworkError -> TranslationResultStatus.YandexNetworkError
                is YandexTranslationResult.Success -> TranslationResultStatus.Success
            }

            translationCallback.receiveTranslation(words, translationResult)
        }
    }
}

sealed class TranslationResultStatus {
    object Success: TranslationResultStatus()
    object YandexNetworkError: TranslationResultStatus()
    object YandexGenericError: TranslationResultStatus()
}
