package com.github.kutyrev.vocabulator.repository.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.datasource.fileparsers.ParserFactory
import com.github.kutyrev.vocabulator.datasource.firebase.CloudBase
import com.github.kutyrev.vocabulator.datasource.remote.YandexSource
import com.github.kutyrev.vocabulator.repository.storage.DefaultStorageRepository
import com.github.kutyrev.vocabulator.repository.translator.DefaultTranslationRepository
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import com.github.kutyrev.vocabulator.repository.translator.TranslationRepository
import com.github.kutyrev.vocabulator.repository.datastore.DataStoreRepository
import com.github.kutyrev.vocabulator.repository.datastore.SettingsRepository
import com.github.kutyrev.vocabulator.repository.file.DefaultFileRepository
import com.github.kutyrev.vocabulator.repository.file.FileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val DATA_STORE_SETTINGS_NAME = "settings"

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    fun provideStorageRepository(vocabulatorDao: VocabulatorDao): StorageRepository {
        return DefaultStorageRepository(vocabulatorDao = vocabulatorDao)
    }

    @Provides
    fun providesTranslationRepository(
        translationBackSource: CloudBase,
        yandexSource: YandexSource
    ): TranslationRepository {
        return DefaultTranslationRepository(translationBackSource, yandexSource)
    }

    @Provides
    @Singleton
    fun providesFileRepository(
        fileParserFactory: ParserFactory,
        settingsRepository: SettingsRepository,
        vocabulatorDao: VocabulatorDao,
        @ApplicationContext appContext: Context
    ): FileRepository {
        return DefaultFileRepository(
            fileParserFactory,
            settingsRepository,
            vocabulatorDao,
            appContext
        )
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(DATA_STORE_SETTINGS_NAME) }
        )
    }

    @Provides
    fun provideSettingsRepository(dateStore: DataStore<Preferences>): SettingsRepository {
        return DataStoreRepository(dateStore)
    }
}
