package com.github.kutyrev.vocabulator.datasource.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface YandexApi {
    @POST("v2/translate")
    suspend fun getTranslation(
        @Header("Authorization") authorization: String,
        @Body requestParams: YandexRequestParams
    ): Response<YandexResult>
}
