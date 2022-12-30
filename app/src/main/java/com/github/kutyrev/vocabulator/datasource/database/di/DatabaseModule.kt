package com.github.kutyrev.vocabulator.datasource.database.di

import com.github.kutyrev.vocabulator.datasource.database.DbDaoMock
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DatabaseModule {
    @Binds
    abstract fun provideVocabulatorDao(dbDaoMock: DbDaoMock): VocabulatorDao
}
