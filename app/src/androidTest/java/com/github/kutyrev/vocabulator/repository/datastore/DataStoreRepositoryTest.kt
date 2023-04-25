package com.github.kutyrev.vocabulator.repository.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_DATASTORE_NAME: String = "test_datastore"

private const val DEFAULT_WORDS_COUNT = 100
private const val NEW_WORDS_COUNT = 200

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
internal class DataStoreRepositoryTest {

    private val testContext: android.content.Context? = ApplicationProvider.getApplicationContext()
    private val testCoroutineDispatcher = StandardTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())
    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = testCoroutineScope,
            produceFile =
            { testContext!!.preferencesDataStoreFile(TEST_DATASTORE_NAME)}
        )
    private val repository: SettingsRepository =
        DataStoreRepository(testDataStore)

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @Test
    fun defaultValuesTest() = runTest {
        assertEquals(false, repository.getLoadPhrasesExamples().first())
        assertEquals(DEFAULT_WORDS_COUNT, repository.getWordsForLoadCount().first())
    }

    @Test
    fun setGetWordsForLoadCountTest() = runTest {
        repository.setWordsForLoadCount(NEW_WORDS_COUNT)
        assertEquals(NEW_WORDS_COUNT, repository.getWordsForLoadCount().first())
    }

    @Test
    fun setLoadPhrasesExamples() = runTest {
        repository.setLoadPhrasesExamples(true)
        assertTrue(repository.getLoadPhrasesExamples().first())
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testCoroutineScope.runTest {
            testDataStore.edit { it.clear() }
        }
        testCoroutineScope.cancel()
    }
}