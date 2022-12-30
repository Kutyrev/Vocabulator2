package com.github.kutyrev.vocabulator.repository.di

import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.repository.DefaultStorageRepository
import com.github.kutyrev.vocabulator.repository.StorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    fun provideStorageRepository(vocabulatorDao: VocabulatorDao): StorageRepository {
        return DefaultStorageRepository(vocabulatorDao = vocabulatorDao)
    }
}
