package com.github.kutyrev.vocabulator.datasource.remote

import com.google.gson.annotations.SerializedName

data class YandexResult(@SerializedName("translations") val translations: List<YandexTranslations>)

data class YandexTranslations(@SerializedName("text") val text: String)
