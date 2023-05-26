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

private const val MOCK_LINE_ONE = "[Script Info]"
private const val MOCK_LINE_TWO = "Title: Default sub file"
private const val MOCK_LINE_THREE = "[V4+ Styles]"
private const val MOCK_LINE_FOUR = "Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding"
private const val MOCK_LINE_FIVE = "Style: ADV VHS (640 x 480),HelveticaNeueLT Std,31,&H0000E3FE,&H000000FF,&H00000000,&H00000000,0,0,0,0,96.4444,100,0,0,1,2.3,0,2,9,9,36,1"
private const val MOCK_LINE_SIX = "Style: Background,Arial,20,&H00FFFFFF,&H000000FF,&H00000000,&H00000000,0,0,0,0,88.8889,100,0,0,1,2,0,5,9,9,10,1"
private const val MOCK_LINE_SEVEN = "[Events]"
private const val MOCK_LINE_EIGHT = "Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text"
private const val MOCK_LINE_NINE = "Dialogue: 0,0:00:02.02,0:00:07.95,ADV VHS (640 x 480),,0,0,0,Opening,Like an angel that has no\\Nsense of mercy..."
private const val MOCK_LINE_TEN = "Dialogue: 0,0:00:07.95,0:00:14.95,ADV VHS (640 x 480),,0,0,0,Opening,Rise, young boy, to the heavens\\Nlike a legend."
private const val RESULT_STRING ="Like an angel that has no sense of mercy... Rise, young boy, to the heavens like a legend. Rise, young boy, to the heavens like a legend. Rise, young boy, to the heavens like a legend."

internal class SsaFileParserTest {
    @Test
    fun parseFileTest() {
        mockkConstructor(ContentResolver::class)
        every { anyConstructed<ContentResolver>().openInputStream(any()) } returns InputStream.nullInputStream()
        mockkConstructor(Scanner::class)
        //This mock realization related to the number of hasNextLine calls in the algorithm
        every { anyConstructed<Scanner>().hasNextLine() } returns
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                false
        every { anyConstructed<Scanner>().nextLine() } returns
                MOCK_LINE_ONE andThen
                MOCK_LINE_TWO andThen
                MOCK_LINE_THREE andThen
                MOCK_LINE_FOUR andThen
                MOCK_LINE_FIVE andThen
                MOCK_LINE_SIX andThen
                MOCK_LINE_SEVEN andThen
                MOCK_LINE_EIGHT andThen
                MOCK_LINE_NINE andThen
                MOCK_LINE_TEN
        val mContextMock = mockk<Context>(relaxed = true)
        mockkStatic(Uri::class)
        val uriMock = mockk<Uri>()

        val ssaFileParser = SsaFileParser(mContextMock)
        val result = ssaFileParser.parseFile(uriMock)

        TestCase.assertTrue(result is ParsingResult.SuccessfullParsing)
        TestCase.assertEquals(RESULT_STRING, (result as ParsingResult.SuccessfullParsing).parsedText)
    }
}
