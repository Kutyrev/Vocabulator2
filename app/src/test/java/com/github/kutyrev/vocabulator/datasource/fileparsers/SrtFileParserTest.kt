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


private const val mockLineOne = "1"
private const val mockLineTwo = "00:00:04,170 --> 00:00:05,838"
private const val mockLineThree = "Man:"
private const val mockLineFour = "Kids, I'm gonna tell you"
private const val mockLineFive = ""
private const val mockLineSix = "2"
private const val mockLineSeven = "00:00:05,840 --> 00:00:06,772"
private const val mockLineEight = "An incredible story;"
private const val mockLineNine = ""
private const val expectedResult = "Man: Kids, I'm gonna tell you An incredible story;"

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
        } returns mockLineOne andThen
                mockLineTwo andThen
                mockLineThree andThen
                mockLineFour andThen
                mockLineFive andThen
                mockLineSix andThen
                mockLineSeven andThen
                mockLineEight andThen
                mockLineNine

        val mContextMock = mockk<Context>(relaxed = true)

        mockkStatic(Uri::class)
        val uriMock = mockk<Uri>()

        val srtFileParser = SrtFileParser(mContextMock)
        val result = srtFileParser.parseFile(uriMock)

        TestCase.assertTrue(result is ParsingResult.SuccessfullParsing)
        TestCase.assertEquals(expectedResult, (result as ParsingResult.SuccessfullParsing).parsedText)
    }
}
