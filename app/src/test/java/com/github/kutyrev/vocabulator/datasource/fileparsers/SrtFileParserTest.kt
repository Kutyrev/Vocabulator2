package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import junit.framework.TestCase
import org.junit.Test
import java.io.InputStream
import java.util.Scanner


private const val MOCK_LINE_ONE = "1"
private const val MOCK_LINE_TWO = "00:00:04,170 --> 00:00:05,838"
private const val MOCK_LINE_THREE = "Man:"
private const val MOCK_LINE_FOUR = "Kids, I'm gonna tell you"
private const val MOCK_LINE_FIVE = ""
private const val MOCK_LINE_SIX = "2"
private const val MOCK_LINE_SEVEN = "00:00:05,840 --> 00:00:06,772"
private const val MOCK_LINE_EIGHT = "An incredible story;"
private const val MOCK_LINE_NINE = ""
private const val EXPECTED_RESULT = "Man: Kids, I'm gonna tell you An incredible story;"

internal class SrtFileParserTest {

    @Test
    fun parseFileTest() {
        mockkConstructor(ContentResolver::class)
        every { anyConstructed<ContentResolver>().openInputStream(any()) } returns InputStream.nullInputStream()
        mockkConstructor(Scanner::class)
        //This mock realization related to the number of hasNextLine calls in the algorithm
        every {
            anyConstructed<Scanner>().hasNextLine()
        } returns true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                false
        every {
            anyConstructed<Scanner>().nextLine()
        } returns MOCK_LINE_ONE andThen
                MOCK_LINE_TWO andThen
                MOCK_LINE_THREE andThen
                MOCK_LINE_FOUR andThen
                MOCK_LINE_FIVE andThen
                MOCK_LINE_SIX andThen
                MOCK_LINE_SEVEN andThen
                MOCK_LINE_EIGHT andThen
                MOCK_LINE_NINE

        val mContextMock = mockk<Context>(relaxed = true)

        mockkStatic(Uri::class)
        val uriMock = mockk<Uri>()

        val srtFileParser = SrtFileParser(mContextMock)
        val result = srtFileParser.parseFile(uriMock)

        TestCase.assertTrue(result is ParsingResult.SuccessfullParsing)
        TestCase.assertEquals(EXPECTED_RESULT, (result as ParsingResult.SuccessfullParsing).parsedText)
    }
}
