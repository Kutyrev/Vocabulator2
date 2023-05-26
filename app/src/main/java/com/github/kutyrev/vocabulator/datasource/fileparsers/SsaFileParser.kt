package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.Context
import android.net.Uri
import java.io.InputStream
import java.util.*

private const val EVENTS_SECTION = "[Events]"
private const val FORMAT_SECTION = "Format:"
private const val DIALOG_SECTION = "Dialogue:"
private const val FORMAT_FIELD_DELIM = ":"
private const val SECTION_DELIMITER = "["
private const val TEXT_SECTION = "Text"
private const val SLASH_CHAR = 65533.toChar().toString()

class SsaFileParser(private val context: Context) : FileParser {

    override fun parseFile(uri: Uri): ParsingResult {
        val subtitlesText = StringBuilder()

        val fileInputStream: InputStream? = context.contentResolver.openInputStream(uri)

        val scanner = Scanner(fileInputStream)

        var lineNumber = 0

        //Find Events section
        while (scanner.hasNextLine()) {
            var nextLine: String = scanner.nextLine()
            lineNumber++
            if (nextLine.trim { it <= ' ' }.equals(EVENTS_SECTION, ignoreCase = true)) {
                nextLine = scanner.nextLine().trim()
                lineNumber++

                //the first line should define the format of the dialogues
                if (!nextLine.startsWith(FORMAT_SECTION)) {
                    //if not, we scan for the format.
                    while (!nextLine.startsWith(FORMAT_SECTION)) {
                        nextLine = scanner.nextLine().trim()
                        lineNumber++
                    }
                }
                // we recover the format's fields
                val dialogueFormat =
                    nextLine.split(FORMAT_FIELD_DELIM).toTypedArray()[1].trim { it <= ' ' }
                        .split(",").toTypedArray()

                //next line
                nextLine = scanner.nextLine().trim()
                lineNumber++
                // we parse each style until we reach a new section
                while (!nextLine.startsWith(SECTION_DELIMITER)) {
                    //we check it is a dialogue
                    //WARNING: all other events are ignored.
                    if (nextLine.startsWith(DIALOG_SECTION)) {
                        //we parse the dialogue
                        subtitlesText.append(" ").append(
                            parseDialogueForASS(
                                nextLine.split(FORMAT_FIELD_DELIM, ignoreCase = false, limit = 2)
                                    .toTypedArray()[1].trim { it <= ' ' }
                                    .split(
                                        ",",
                                        ignoreCase = false,
                                        limit = dialogueFormat.size.coerceAtLeast(0)
                                    )
                                    .toTypedArray(), dialogueFormat))
                    }
                    //next line
                    if (scanner.hasNextLine()) {
                        nextLine = scanner.nextLine().trim()
                        lineNumber++
                    } else break
                }
            }
        }

        fileInputStream?.close()

        return ParsingResult.SuccessfullParsing(subtitlesText.toString().trim())
    }

    /**
     * This methods transforms a dialogue line from ASS according to a format definition into an Caption object.
     *
     * @param line the dialogue line without its declaration
     * @param dialogueFormat the list of attributes in this dialogue line
     * @return String
     */
    private fun parseDialogueForASS(line: Array<String>, dialogueFormat: Array<String>): String {
        for (i in dialogueFormat.indices) {
            val trimmedDialogueFormat = dialogueFormat[i].trim { it <= ' ' }
            if (trimmedDialogueFormat.equals(TEXT_SECTION, ignoreCase = true)) {
                //we save the text
                val captionText = line[i]

                //text is cleaned before being inserted into the caption
                return captionText.replace("\\{.*?\\}".toRegex(), "").replace("\n", " ")
                    .replace("\\N", " ").replace(SLASH_CHAR, "'")
            }
        }
        return ""
    }
}
