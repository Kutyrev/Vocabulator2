package com.github.kutyrev.vocabulator.datasource.remote

import com.google.gson.annotations.SerializedName

data class YandexResult(@SerializedName("translations") val translations: List<YandexTranslation>)

data class YandexTranslation(@SerializedName("text") val text: String)
