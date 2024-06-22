package com.github.kutyrev.vocabulator.repository.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.github.kutyrev.vocabulator.MainDispatcherRule
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.datasource.fileparsers.FileParser
import com.github.kutyrev.vocabulator.datasource.fileparsers.ParserFactory
import com.github.kutyrev.vocabulator.datasource.fileparsers.ParsingResult
import com.github.kutyrev.vocabulator.features.editsub.model.EditableWordCard
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SupportedFileExtension
import com.github.kutyrev.vocabulator.repository.datastore.SettingsRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.OutputStream

private const val CORRECT_WORDCARDS_SIZE = 2
private const val CORRECT_WORDCARDS_SIZE_REPARSE = 1
private const val LINE_NUMBER_WITH_ERROR = 1
private const val CORRECT_ORIG_WORD_REPARSE = "mock2"

internal class DefaultFileRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var vocabulatorDao: VocabulatorDao

    @MockK
    lateinit var settingsRepository: SettingsRepository

    @MockK
    lateinit var fileParserFactory: ParserFactory

    @MockK
    lateinit var fileParser: FileParser

    private val contextMock = mockk<Context>(relaxed = true)

    private val commonWordsMock = listOf(CommonWord(1, 1, "mock"))
    private val commonWordsMockReparse = listOf(
        CommonWord(1, 1, "mock"),
        CommonWord(2, 1, "mock1")
    )
    private val parsedTextMock = "Mock. Mock1! Mock2?"
    private val language = Language.EN
    private val filenameMock = "subs.srt"
    private val filenameIllegalMock = "subs.aab"
    private val uriMock = mockk<Uri>()
    private val editableWord = EditableWordCard(1, 1, "word", "cuvant")

    @Before
    fun mockStaticFunctions() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        mockkStatic(TextUtils::class)
        every { TextUtils.isDigitsOnly(any()) } returns false
    }

    @Test
    fun parseFileSuccessTest() = runTest {
        coEvery {
            vocabulatorDao.getCommonWords(any())
        } returns commonWordsMock

        coEvery {
            fileParserFactory.getParser(any())
        } returns fileParser

        coEvery {
            fileParser.parseFile(any())
        } returns ParsingResult.SuccessfullParsing(parsedTextMock)

        coEvery {
            settingsRepository.getLoadPhrasesExamples()
        } returns flowOf(true)

        coEvery {
            settingsRepository.getWordsForLoadCount()
        } returns flowOf(100)

        val defaultFileRepository =
            DefaultFileRepository(
                fileParserFactory,
                settingsRepository,
                vocabulatorDao,
                contextMock
            )

        val result = defaultFileRepository.parseFile(uriMock, language, filenameMock)

        assertTrue(result is FileLoadStatus.FileLoaded)
        assertEquals(
            CORRECT_WORDCARDS_SIZE,
            (result as FileLoadStatus.FileLoaded).subtitles.wordCards.size
        )

        coVerify {
            vocabulatorDao.getCommonWords(language.ordinal)
            fileParserFactory.getParser(SupportedFileExtension.SRT)
            fileParser.parseFile(uriMock)
            settingsRepository.getLoadPhrasesExamples()
            settingsRepository.getWordsForLoadCount()
        }
    }

    @Test
    fun parseFileIllegalExtensionTest() = runTest {
        coEvery {
            vocabulatorDao.getCommonWords(any())
        } returns commonWordsMock

        coEvery {
            fileParserFactory.getParser(any())
        } returns fileParser

        val defaultFileRepository =
            DefaultFileRepository(
                fileParserFactory,
                settingsRepository,
                vocabulatorDao,
                contextMock
            )

        val result = defaultFileRepository.parseFile(uriMock, language, filenameIllegalMock)

        assertTrue(result is FileLoadStatus.LoadingError)
        assertEquals(
            FileLoadError.UnsupportedFileExtension,
            (result as FileLoadStatus.LoadingError).error
        )

        coVerify {
            vocabulatorDao.getCommonWords(language.ordinal)
        }
    }

    @Test
    fun parseFileIOExceptionTest() = runTest {
        coEvery {
            vocabulatorDao.getCommonWords(any())
        } returns commonWordsMock

        coEvery {
            fileParserFactory.getParser(any())
        } returns fileParser

        coEvery {
            fileParser.parseFile(any())
        } returns ParsingResult.IOException

        val defaultFileRepository =
            DefaultFileRepository(
                fileParserFactory,
                settingsRepository,
                vocabulatorDao,
                contextMock
            )

        val result = defaultFileRepository.parseFile(uriMock, language, filenameMock)

        assertTrue(result is FileLoadStatus.LoadingError)
        assertEquals(
            FileLoadError.IOException,
            (result as FileLoadStatus.LoadingError).error
        )

        coVerify {
            vocabulatorDao.getCommonWords(language.ordinal)
            fileParserFactory.getParser(SupportedFileExtension.SRT)
            fileParser.parseFile(uriMock)
        }
    }

    @Test
    fun parseFileInvalidTimestampFormatExceptionTest() = runTest {
        coEvery {
            vocabulatorDao.getCommonWords(any())
        } returns commonWordsMock

        coEvery {
            fileParserFactory.getParser(any())
        } returns fileParser

        coEvery {
            fileParser.parseFile(any())
        } returns ParsingResult.InvalidTimestampFormatException(LINE_NUMBER_WITH_ERROR, "")

        val defaultFileRepository =
            DefaultFileRepository(
                fileParserFactory,
                settingsRepository,
                vocabulatorDao,
                contextMock
            )

        val result = defaultFileRepository.parseFile(uriMock, language, filenameMock)

        assertTrue(result is FileLoadStatus.LoadingError)
        assertEquals(
            FileLoadError.FileFormatCorrupted,
            (result as FileLoadStatus.LoadingError).error
        )

        coVerify {
            vocabulatorDao.getCommonWords(language.ordinal)
            fileParserFactory.getParser(SupportedFileExtension.SRT)
            fileParser.parseFile(uriMock)
        }
    }

    @Test
    fun reparseSubtitlesTest() = runTest {
        coEvery {
            vocabulatorDao.getCommonWords(any())
        } returns commonWordsMock

        coEvery {
            settingsRepository.getLoadPhrasesExamples()
        } returns flowOf(true)

        coEvery {
            settingsRepository.getWordsForLoadCount()
        } returns flowOf(100)

        coEvery {
            fileParserFactory.getParser(any())
        } returns fileParser

        coEvery {
            fileParser.parseFile(any())
        } returns ParsingResult.SuccessfullParsing(parsedTextMock)

        val defaultFileRepository =
            DefaultFileRepository(
                fileParserFactory,
                settingsRepository,
                vocabulatorDao,
                contextMock
            )

        val result = defaultFileRepository.parseFile(uriMock, language, filenameMock)

        coEvery {
            vocabulatorDao.getCommonWords(any())
        } returns commonWordsMockReparse

        val wordCardsResult =
            defaultFileRepository.reparseSubtitles((result as FileLoadStatus.FileLoaded).subtitles)

        assertEquals(CORRECT_WORDCARDS_SIZE_REPARSE, wordCardsResult.size)
        assertEquals(CORRECT_ORIG_WORD_REPARSE, wordCardsResult[0].originalWord)
    }

    @Test
    fun exportSubtitlesTest() = runTest {

        val localContextMock = mockk<Context>()
        val contentResolverMock = mockk<ContentResolver>()
        val outputStreamMock = mockk<OutputStream>(relaxed = true)

        val dummyUri = mockk<Uri>()

        every { localContextMock.contentResolver } returns contentResolverMock

        every { contentResolverMock.openOutputStream(dummyUri) } returns outputStreamMock

        val defaultFileRepository =
            DefaultFileRepository(
                fileParserFactory,
                settingsRepository,
                vocabulatorDao,
                localContextMock
            )

        val result = defaultFileRepository.exportSubtitles(dummyUri, listOf(editableWord))
        assertEquals(FileExportStatus.Success, result)
        print(result)
    }
}
