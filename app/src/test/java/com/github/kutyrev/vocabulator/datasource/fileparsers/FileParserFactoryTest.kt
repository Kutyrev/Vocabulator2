package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.Context
import com.github.kutyrev.vocabulator.model.SupportedFileExtension
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import org.junit.Test

internal class FileParserFactoryTest {

    val mContextMock = mockk<Context>(relaxed = true)

    @Test
    fun getParserTest() {
        val parserFactory = FileParserFactory(mContextMock)
        var parser : FileParser? = parserFactory.getParser(SupportedFileExtension.SRT)
        assertTrue(parser is SrtFileParser)
        parser = parserFactory.getParser(SupportedFileExtension.FB2)
        assertTrue(parser is Fb2FileParser)
        parser = parserFactory.getParser(SupportedFileExtension.SSA)
        assertTrue(parser is SsaFileParser)
        parser = parserFactory.getParser(SupportedFileExtension.TXT)
        assertTrue(parser is TxtFileParser)
        parser = parserFactory.getParser(SupportedFileExtension.ASS)
        assertTrue(parser is SsaFileParser)
    }
}
