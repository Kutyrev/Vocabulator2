package com.github.kutyrev.vocabulator.repository.file

import android.net.Uri
import com.github.kutyrev.vocabulator.datasource.fileparsers.*
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.SupportedFileExtension
import javax.inject.Inject

class DefaultFileRepository @Inject constructor(private val fileParserFactory: ParserFactory) :
    FileRepository {

    override suspend fun parseFile(uri: Uri, language: Language, fileName: String): FileLoadStatus {
        //Log.d("FileLoad", String.valueOf(System.currentTimeMillis()));

        val commonWordsArray: MutableList<String> = getCommonWords(language)

        val commonWords: HashSet<String> = HashSet()
        commonWords.addAll(commonWordsArray)

        val extension = fileName.substring(fileName.length - 3).uppercase()
        val newSubtitleEntry =
            SubtitlesUnit(0, fileName, language.ordinal, Language.EN.ordinal)
        var subtitlesText = ""

        val fileLoadParser = fileParserFactory.getParser(SupportedFileExtension.valueOf(extension))
            ?: return FileLoadStatus.LoadingError(FileLoadError.UnsupportedFileExtension)

        when (val parsingResult = fileLoadParser.parseFile(uri)) {
            is ParsingResult.SuccessfullParsing -> subtitlesText =
                parsingResult.parsedText
            is ParsingResult.InvalidTimestampFormatException -> return FileLoadStatus.LoadingError(
                FileLoadError.FileFormatCorrupted, parsingResult.lineNumber, parsingResult.line
            )
        }

        return FileLoadStatus.FileLoaded(newSubtitleEntry)
    }

    private fun getCommonWords(language: Language): MutableList<String> {
        return mutableListOf<String>("I", "you") //TODO Implementation
    }
}
