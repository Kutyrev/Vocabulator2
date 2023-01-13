package com.github.kutyrev.vocabulator.repository.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.datasource.translators.TranslationBackSource
import com.github.kutyrev.vocabulator.repository.DefaultStorageRepository
import com.github.kutyrev.vocabulator.repository.DefaultTranslationRepository
import com.github.kutyrev.vocabulator.repository.StorageRepository
import com.github.kutyrev.vocabulator.repository.TranslationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val DATA_STORE_SETTINGS_NAME = "settings"

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    fun provideStorageRepository(vocabulatorDao: VocabulatorDao): StorageRepository {
        return DefaultStorageRepository(vocabulatorDao = vocabulatorDao)
    }

    @Provides
    fun providesTranslationRepository(translationBackSource: TranslationBackSource): TranslationRepository {
        return DefaultTranslationRepository(translationBackSource)
    }

    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(DATA_STORE_SETTINGS_NAME) }
        )
    }
}
