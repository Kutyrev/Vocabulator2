package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.Context
import com.github.kutyrev.vocabulator.model.SupportedFileExtension
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FileParserFactory @Inject constructor(@ApplicationContext private val context: Context) :
    ParserFactory {

    override fun getParser(fileExtension: SupportedFileExtension): FileParser =
        when (fileExtension) {
            SupportedFileExtension.SRT -> SrtFileParser(context)
            SupportedFileExtension.SSA -> SsaFileParser(context)
            SupportedFileExtension.TXT -> TxtFileParser(context)
            SupportedFileExtension.FB2 -> Fb2FileParser(context)
            SupportedFileExtension.ASS -> SsaFileParser(context)
        }
}
