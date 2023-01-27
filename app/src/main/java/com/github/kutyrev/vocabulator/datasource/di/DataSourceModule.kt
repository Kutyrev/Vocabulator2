package com.github.kutyrev.vocabulator.datasource.di

import android.content.Context
import com.github.kutyrev.vocabulator.datasource.fileparsers.FileParserFactory
import com.github.kutyrev.vocabulator.datasource.fileparsers.ParserFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DataSourceModule {
    @Provides
    fun providesFileParserFactory(@ApplicationContext appContext: Context): ParserFactory {
        return FileParserFactory(appContext)
    }
}
