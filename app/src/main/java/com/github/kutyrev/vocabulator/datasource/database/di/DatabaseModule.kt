package com.github.kutyrev.vocabulator.datasource.database.di

import android.content.Context
import androidx.room.Room
import com.github.kutyrev.vocabulator.datasource.database.DatabaseSource
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideVocabulatorDao(databaseSource: DatabaseSource): VocabulatorDao {
        return databaseSource.vocabulatorDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): DatabaseSource {
        return Room.databaseBuilder(
            appContext,
            DatabaseSource::class.java,
            "vocabulator.db"
        ).createFromAsset("initial_commons.db").build()
    }
}
