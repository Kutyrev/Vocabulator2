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

private const val mockLineOne = "Test data."
private const val mockLineTwo = "Test data!"
private const val resultString = "Test data. Test data!"

internal class TxtFileParserTest {

    @Test
    fun parseFileTest() {
        mockkConstructor(ContentResolver::class)
        every { anyConstructed<ContentResolver>().openInputStream(any()) } returns InputStream.nullInputStream()
        mockkConstructor(Scanner::class)
        every { anyConstructed<Scanner>().hasNextLine() } returns true andThen true andThen false
        every { anyConstructed<Scanner>().nextLine() } returns mockLineOne andThen mockLineTwo
        val mContextMock = mockk<Context>(relaxed = true)

        mockkStatic(Uri::class)
        val uriMock = mockk<Uri>()

        val txtFileParser = TxtFileParser(mContextMock)
        val result = txtFileParser.parseFile(uriMock)

        assertTrue(result is ParsingResult.SuccessfullParsing)
        assertEquals(resultString, (result as ParsingResult.SuccessfullParsing).parsedText)
    }
}
