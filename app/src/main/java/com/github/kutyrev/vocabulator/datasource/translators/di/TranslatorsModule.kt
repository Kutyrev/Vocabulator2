package com.github.kutyrev.vocabulator.datasource.translators.di

import com.github.kutyrev.vocabulator.datasource.translators.TranslationBackSource
import com.github.kutyrev.vocabulator.datasource.translators.VocabulatorBackSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    fun bindsTranslationDataSource(): TranslationBackSource {
        return VocabulatorBackSource()
    }
}
