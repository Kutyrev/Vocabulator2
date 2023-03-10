package com.github.kutyrev.vocabulator.datasource.remote.di

import com.github.kutyrev.vocabulator.datasource.remote.YandexApi
import com.github.kutyrev.vocabulator.datasource.remote.YandexDataSource
import com.github.kutyrev.vocabulator.datasource.remote.YandexSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val YANDEX_BASE_URL = "https://translate.api.cloud.yandex.net/translate/"

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {
    @Provides
    @Singleton
    fun providesDefaultOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
    }

    @Provides
    @Singleton
    fun providesMockRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(YANDEX_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun providesYandexDataSource(yandexApi: YandexApi): YandexSource {
        return YandexDataSource(yandexApi)
    }
}
