package com.github.kutyrev.vocabulator.repository.file

import android.net.Uri
import com.github.kutyrev.vocabulator.app.di.IoDispatcher
import com.github.kutyrev.vocabulator.datasource.database.VocabulatorDao
import com.github.kutyrev.vocabulator.datasource.fileparsers.*
import com.github.kutyrev.vocabulator.model.*
import com.github.kutyrev.vocabulator.repository.datastore.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class DefaultFileRepository @Inject constructor(
    private val fileParserFactory: ParserFactory,
    private val settingsRepository: SettingsRepository,
    private val vocabulatorDao: VocabulatorDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    FileRepository {

    override suspend fun parseFile(uri: Uri, language: Language, fileName: String): FileLoadStatus =
        //Log.d("FileLoad", String.valueOf(System.currentTimeMillis()));

        withContext(dispatcher) {
            val commonWordsArray: List<CommonWord> = getCommonWords(language)

            val commonWords: HashSet<String> = HashSet()
            commonWordsArray.forEach { commonWords.add(it.word) }

            val extension = fileName.substring(fileName.length - 3).uppercase()
            val newSubtitleEntry =
                SubtitlesUnit(0, fileName, language.ordinal, Language.EN.ordinal)
            var subtitlesText = ""

            val fileLoadParser =
                fileParserFactory.getParser(SupportedFileExtension.valueOf(extension))
                    ?: return@withContext FileLoadStatus.LoadingError(FileLoadError.UnsupportedFileExtension)

            when (val parsingResult = fileLoadParser.parseFile(uri)) {
                is ParsingResult.SuccessfullParsing -> subtitlesText =
                    parsingResult.parsedText
                is ParsingResult.InvalidTimestampFormatException -> return@withContext FileLoadStatus.LoadingError(
                    FileLoadError.FileFormatCorrupted, parsingResult.lineNumber, parsingResult.line
                )
                ParsingResult.IOException -> FileLoadStatus.LoadingError(FileLoadError.IOException)
            }

            //Удаление имён собственных
            //<editor-fold desc="Proper name detection">
            val wordsArray = subtitlesText.split(" ").toTypedArray()
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
                        wordsArray[ind] = wordsArray[ind] + " "
                        wasPreviousDeleted = false
                    }
                } else {
                    wordsArray[ind] = wordsArray[ind] + " "
                    wasPreviousDeleted = false
                }

            }
            //</editor-fold>

            val subtitlesTextBuilder: StringBuilder = StringBuilder()

            for (ind in wordsArray.indices) {
                if (ind > 0) {
                    //subtitlesText = subtitlesText.concat(wordsArray[ind]);
                    subtitlesTextBuilder.append(wordsArray[ind])
                }
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
            val sortedWords = freqMap.toList().sortedBy { (_, value) -> value }.reversed()
                .sortedBy { (key, _) -> key }.toMap()

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

                newSubtitleEntry.wordCards.add(WordCard(EMPTY_SUBS_ID, wordEntry.key, ""))
            }

            return@withContext FileLoadStatus.FileLoaded(newSubtitleEntry)
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
        return curChar == '.' || curChar == '?' || curChar == '!' || curChar == '-'
    }

    private fun isCaseException(word: String): Boolean {
        return word[0] == 'I'

        /*
       Артефакты:
        Слово начинается с "
        Слово начитается с -
        */
    }

    private fun getCommonWords(language: Language): List<CommonWord> =
        vocabulatorDao.getCommonWords(language.ordinal)
}
