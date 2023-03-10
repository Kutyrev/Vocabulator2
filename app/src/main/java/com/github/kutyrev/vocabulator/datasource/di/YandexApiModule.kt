package com.github.kutyrev.vocabulator.datasource.di

import com.github.kutyrev.vocabulator.datasource.remote.YandexApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object YandexApiModule {

    @Provides
    fun provideApiService(
        retrofit: Retrofit
    ): YandexApi = retrofit.create(YandexApi::class.java)
}
