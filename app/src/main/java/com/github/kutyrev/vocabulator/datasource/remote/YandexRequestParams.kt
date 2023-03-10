package com.github.kutyrev.vocabulator.datasource.remote

private const val FORMAT = "PLAIN_TEXT"

//Docs: https://cloud.yandex.com/en/docs/translate/api-ref/Translation/translate
data class YandexRequestParams(
    val sourceLanguageCode: String,
    val targetLanguageCode: String,
    val format: String = FORMAT,
    val texts: List<String>
)
