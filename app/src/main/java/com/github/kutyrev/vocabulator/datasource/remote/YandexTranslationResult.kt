package com.github.kutyrev.vocabulator.datasource.remote

import com.github.kutyrev.vocabulator.model.WordCard

sealed class YandexTranslationResult {
    class Success(val translatedCards: List<WordCard>): YandexTranslationResult()
    class GenericError(val errorCode: Int): YandexTranslationResult()
    object NetworkError: YandexTranslationResult()
}
