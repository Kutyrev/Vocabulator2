package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.Context
import android.net.Uri
import android.util.Log
import com.github.kutyrev.vocabulator.BuildConfig
import java.io.InputStream
import java.util.*

private const val TAG = "SrtFileParser"
private const val UNEXPECTED_END_OF_FILE_1_MSG = "Unexpected end of file #1"
private const val UNEXPECTED_END_OF_FILE_2_MSG = "Unexpected end of file #2"
private const val UNEXPECTED_END_OF_FILE_3_MSG = "Unexpected end of file #3"
private const val UNEXPECTED_END_OF_FILE_4_MSG = "Unexpected end of file #4"
private const val TIMESTAMP_DELIMITER = " --> "

class SrtFileParser(private val context: Context) : FileParser {

    override fun parseFile(uri: Uri): ParsingResult {
        val subtitlesText = StringBuilder()

        val fileInputStream: InputStream? = context.contentResolver.openInputStream(uri)
        //FileInputStream in = new FileInputStream(context.getContentResolver()
        // .openFileDescriptor(uri, "r").getFileDescriptor());
        //Through FileInputStream, the load was interrupted after a small piece of the file

        val scanner = Scanner(fileInputStream)

        var lineNumber = 0
        var skipNext = true
        var line: String

        while (scanner.hasNextLine()) {
            if (skipNext) {
                //There can be more than one empty line after the line with text
                line = scanner.nextLine()
                lineNumber++
                while (line.isEmpty()) {
                    if (scanner.hasNextLine()) {
                        line = scanner.nextLine()
                        lineNumber++
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, UNEXPECTED_END_OF_FILE_4_MSG)
                        }
                        break
                    }
                }
            } else {
                skipNext = true
            }
            /* Read the Timestamps from the file. */
            val nextLine: String = scanner.nextLine()
            lineNumber++
            val timestamps = nextLine.split(TIMESTAMP_DELIMITER).toTypedArray()
            if (timestamps.size != 2) {
                return ParsingResult.InvalidTimestampFormatException(lineNumber, nextLine)
            }

            line = if (scanner.hasNextLine()) scanner.nextLine() else {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, UNEXPECTED_END_OF_FILE_1_MSG)
                }
                break
            }
            lineNumber++
            if (!line.isEmpty()) {
                while (!line.isEmpty()) {
                    //Multiple lines of text
                    subtitlesText.append(" ").append(line)
                    line = if (scanner.hasNextLine()) scanner.nextLine() else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, UNEXPECTED_END_OF_FILE_2_MSG)
                        }
                        break
                    }
                    lineNumber++
                }
            } else {
                //Can be just blank lines instead of text
                while (line.isEmpty()) {
                    if (scanner.hasNextLine()) {
                        line = scanner.nextLine()
                        lineNumber++
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, UNEXPECTED_END_OF_FILE_3_MSG)
                        }
                        break
                    }
                    skipNext = false //Current row is id
                }
            }
        }

        fileInputStream?.close()

        return ParsingResult.SuccessfullParsing(subtitlesText.toString().trim())
    }
}
