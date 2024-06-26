package com.github.kutyrev.vocabulator.repository.file

import android.content.Context
import android.net.Uri
import android.util.Log
import com.github.kutyrev.vocabulator.BuildConfig
import com.github.kutyrev.vocabulator.app.di.IoDispatcher
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.datasource.fileparsers.*
import com.github.kutyrev.vocabulator.features.editsub.model.EditableWordCard
import com.github.kutyrev.vocabulator.model.*
import com.github.kutyrev.vocabulator.repository.datastore.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

private const val POINT = '.'
private const val DASH_SIGN = '-'
private const val UPPER_COMMA = '\''
private const val CSV_FILE_HEADER = "Word;Translated word;Example"
private const val ROW_SEPARATOR = "\n"
private const val CSV_WORDS_SEPARATOR = ";"
private const val SEMICOLON_REPLACEMENT = "(semicolon)"

class DefaultFileRepository @Inject constructor(
    private val fileParserFactory: ParserFactory,
    private val settingsRepository: SettingsRepository,
    private val vocabulatorDao: VocabulatorDao,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    FileRepository {

    override var sortedWords: Map<String, Int> = mapOf()
    private val phrasesArray: MutableList<String> = mutableListOf()

    override suspend fun parseFile(uri: Uri, language: Language, fileName: String): FileLoadStatus =
        //Log.d("FileLoad", String.valueOf(System.currentTimeMillis()));

        withContext(dispatcher) {
            val commonWordsArray: List<CommonWord> = getCommonWords(language)

            val commonWords: HashSet<String> = HashSet()
            commonWordsArray.forEach { commonWords.add(it.word) }

            val extension = fileName.substringAfterLast(POINT).uppercase()
            val newSubtitleEntry =
                SubtitlesUnit(0, fileName, language.ordinal, Language.EN.ordinal)
            var subtitlesText = ""

            val fileLoadParser = try {
                fileParserFactory.getParser(SupportedFileExtension.valueOf(extension))
                    ?: return@withContext FileLoadStatus.LoadingError(FileLoadError.UnsupportedFileExtension)
            } catch (_: java.lang.IllegalArgumentException) {
                return@withContext FileLoadStatus.LoadingError(FileLoadError.UnsupportedFileExtension)
            }

            when (val parsingResult = fileLoadParser.parseFile(uri)) {
                is ParsingResult.SuccessfullParsing -> subtitlesText =
                    parsingResult.parsedText

                is ParsingResult.InvalidTimestampFormatException -> return@withContext FileLoadStatus.LoadingError(
                    FileLoadError.FileFormatCorrupted, parsingResult.lineNumber, parsingResult.line
                )

                ParsingResult.IOException -> return@withContext FileLoadStatus.LoadingError(
                    FileLoadError.IOException
                )
            }

            //Удаление имён собственных
            //<editor-fold desc="Proper name detection">
            val wordsArray = subtitlesText.split(" ").toTypedArray()

            val isLoadPhrases = settingsRepository.getLoadPhrasesExamples().first()
            if (isLoadPhrases) {
                //val phrasesArray = subtitlesText.split(". ", "? ", "! ").toTypedArray()
                val pattern = Pattern.compile("(?<=[.!?])\\s+")
                phrasesArray.clear()
                phrasesArray.addAll(subtitlesText.split(pattern))
                if (BuildConfig.DEBUG) {
                    phrasesArray.forEach { Log.d("DefaultFileRepository", it) }
                }
            }

            var wasPreviousDeleted = false

            for (ind in wordsArray.indices) {
                if (ind > 0 && wordsArray[ind].isNotEmpty()) {
                    if ((wordsArray[ind][0] == wordsArray[ind].uppercase(Locale.getDefault())[0]
                                ) and (!isEndOfPhrase(wordsArray[ind - 1]) || wasPreviousDeleted)
                        and !isCaseException(wordsArray[ind])
                        and !Character.isDigit(wordsArray[ind][0])
                    ) {
                        wasPreviousDeleted =
                            !isEndOfPhrase(wordsArray[ind]) //Само имя собственное может быть концом фразы
                        wordsArray[ind] = ""
                    } else {
                        wasPreviousDeleted = false
                    }
                } else {
                    wasPreviousDeleted = false
                }
            }
            //</editor-fold>

            wordsCleaning(wordsArray)

            val subtitlesTextBuilder: StringBuilder = StringBuilder()

            for (ind in wordsArray.indices) {
                ///if (ind > 0) {
                //subtitlesText = subtitlesText.concat(wordsArray[ind]);
                if (wordsArray[ind].isNotEmpty()) {
                    subtitlesTextBuilder.append(wordsArray[ind]).append(" ")
                }
                //}
            }

            subtitlesText = subtitlesTextBuilder.toString()

            // "один или более символ, не являющийся ни буквой, ни цифрой, ни символами -'"
            val textScanner = Scanner(subtitlesText)
                .useDelimiter("[^\\p{L}\\p{Digit}[-']]+")

            // Пройдем по всем словам входного потока и составим Map<String, Integer>,
            // где ключом является слово, преобразованное в нижний регистр,
            // а значением — частота этого слова.
            val freqMap: MutableMap<String, Int> = HashMap()

            textScanner.forEachRemaining { s: String ->
                freqMap.merge(
                    s.lowercase(Locale.getDefault()), 1
                ) { a: Int, b: Int -> a + b }
            }

            //  Cначала упорядочивает пары частоте (по убыванию),
            //  а затем по слову (в алфавитном порядке).
            sortedWords =
                freqMap.toList().sortedBy { (key, _) -> key }.sortedBy { (_, value) -> value }
                    .reversed().toMap()

            var limit = settingsRepository.getWordsForLoadCount().first()

            for (wordEntry in sortedWords) {
                if (limit-- == 0) break

                if (commonWords.contains(wordEntry.key)
                    || (wordEntry.key.length == 1)
                    || (android.text.TextUtils.isDigitsOnly(wordEntry.key))
                ) {
                    limit++
                    continue
                }

                if (isLoadPhrases) {
                    val foundedWord = phrasesArray.find {
                        it.contains(
                            StringBuilder(" ").append(wordEntry.key).append(" "),
                            true
                        )
                    }

                    newSubtitleEntry.wordCards.add(
                        WordCard(
                            EMPTY_SUBS_ID,
                            wordEntry.key,
                            "",
                            wordEntry.value,
                            foundedWord ?: ""
                        )
                    )
                } else {
                    newSubtitleEntry.wordCards.add(
                        WordCard(
                            EMPTY_SUBS_ID,
                            wordEntry.key,
                            "",
                            wordEntry.value
                        )
                    )
                }
            }

            return@withContext FileLoadStatus.FileLoaded(newSubtitleEntry)
        }

    private fun wordsCleaning(wordsArray: Array<String>) {
        //Sometimes in texts we have words with commas like "couldn’t" instead of "couldn't"
        //these commas "’" can't be in a key of a hashmap, so in a key we have something
        //like "couldn"
        for (ind in wordsArray.indices) {
            wordsArray[ind] = wordsArray[ind].replace("’", "'")
            if (wordsArray[ind].isNotEmpty()) {
                if (wordsArray[ind].startsWith(DASH_SIGN) ||
                    wordsArray[ind].startsWith(UPPER_COMMA)
                ) {
                    wordsArray[ind] = wordsArray[ind].drop(1)
                }
                if (wordsArray[ind].endsWith(DASH_SIGN) ||
                    wordsArray[ind].endsWith(UPPER_COMMA)
                ) {
                    wordsArray[ind] = wordsArray[ind].dropLast(1)
                }
            }
        }
    }

    override suspend fun reparseSubtitles(subtitlesUnit: SubtitlesUnit): List<WordCard> =
        withContext(dispatcher) {
            val commonWordsArray: List<CommonWord> =
                getCommonWords(Language.entries[subtitlesUnit.origLangId])

            val wordCards: MutableList<WordCard> = mutableListOf()

            val commonWords: HashSet<String> = HashSet()

            commonWordsArray.forEach { commonWords.add(it.word) }
            var limit = settingsRepository.getWordsForLoadCount().first()
            val isLoadPhrases = settingsRepository.getLoadPhrasesExamples().first()

            for (wordEntry in sortedWords) {
                if (limit-- == 0) break

                if (commonWords.contains(wordEntry.key)
                    || (wordEntry.key.length == 1)
                    || (android.text.TextUtils.isDigitsOnly(wordEntry.key))
                ) {
                    limit++
                    continue
                }

                if (isLoadPhrases) {
                    val foundedWord = phrasesArray.find {
                        it.contains(
                            StringBuilder(" ").append(wordEntry.key).append(" "), true
                        )
                    }
                    wordCards.add(
                        WordCard(
                            subtitlesUnit.id,
                            wordEntry.key,
                            "",
                            wordEntry.value,
                            foundedWord ?: ""
                        )
                    )
                } else {
                    wordCards.add(
                        WordCard(
                            subtitlesUnit.id,
                            wordEntry.key,
                            "",
                            wordEntry.value
                        )
                    )
                }
            }
            return@withContext wordCards
        }

    override suspend fun exportSubtitles(
        uri: Uri,
        words: List<EditableWordCard>
    ): FileExportStatus {
        context.contentResolver.openOutputStream(uri)?.let { outputStream ->
            try {
                val writer = OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
                writer.append(CSV_FILE_HEADER).append(ROW_SEPARATOR)
                words.forEach { row ->
                    writer.append(
                        listOf(
                            row.originalWord,
                            row.translatedWord.replace(
                                CSV_WORDS_SEPARATOR,
                                SEMICOLON_REPLACEMENT
                            ),
                            row.phrase.replace(
                                CSV_WORDS_SEPARATOR,
                                SEMICOLON_REPLACEMENT
                            )
                        ).joinToString(
                            CSV_WORDS_SEPARATOR
                        )
                    ).append(ROW_SEPARATOR)
                }
                writer.flush()
                writer.close()
                return FileExportStatus.Success
            } catch (e: IOException) {
                return FileExportStatus.Error
            }
        }
        return FileExportStatus.Error
    }

    private fun isEndOfPhrase(word: String): Boolean {
        val wordLength = word.length
        if (wordLength == 0) {
            return true
        }
        var curChar = word[wordLength - 1]
        if (curChar == ' ' && wordLength > 1) {
            curChar = word[wordLength - 2]
        }

        //Dialog
        return curChar == '.' || curChar == '?' || curChar == '!' || curChar == DASH_SIGN
    }

    private fun isCaseException(word: String): Boolean {
        return word[0] == 'I'

        /*
       Artefacts:
        Word starts with "
        Word starts with -
        */
    }

    private fun getCommonWords(language: Language): List<CommonWord> =
        vocabulatorDao.getCommonWords(language.ordinal)
}
