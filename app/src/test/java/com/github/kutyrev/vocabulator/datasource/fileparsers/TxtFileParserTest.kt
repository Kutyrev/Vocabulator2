package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.InputStream
import java.util.Scanner

private const val MOCK_LINE_ONE = "Test data."
private const val MOCK_LINE_TWO = "Test data!"
private const val RESULT_STRING = "Test data. Test data!"

internal class TxtFileParserTest {

    @Test
    fun parseFileTest() {
        mockkConstructor(ContentResolver::class)
        every { anyConstructed<ContentResolver>().openInputStream(any()) } returns InputStream.nullInputStream()
        mockkConstructor(Scanner::class)
        //This mock realization related to the number of hasNextLine calls in the algorithm
        every { anyConstructed<Scanner>().hasNextLine() } returns true andThen true andThen false
        every { anyConstructed<Scanner>().nextLine() } returns MOCK_LINE_ONE andThen MOCK_LINE_TWO
        val mContextMock = mockk<Context>(relaxed = true)

        mockkStatic(Uri::class)
        val uriMock = mockk<Uri>()

        val txtFileParser = TxtFileParser(mContextMock)
        val result = txtFileParser.parseFile(uriMock)

        assertTrue(result is ParsingResult.SuccessfullParsing)
        assertEquals(RESULT_STRING, (result as ParsingResult.SuccessfullParsing).parsedText)
    }
}
