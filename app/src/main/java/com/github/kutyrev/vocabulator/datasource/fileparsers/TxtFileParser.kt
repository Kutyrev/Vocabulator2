package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.Context
import android.net.Uri
import java.io.InputStream
import java.util.*

class TxtFileParser(private val context: Context) : FileParser {

    override fun parseFile(uri: Uri): ParsingResult {
        val fileText = StringBuilder()

        val fileInputStream: InputStream? = context.contentResolver.openInputStream(uri)

        val scanner = Scanner(fileInputStream)
        var line: String

        while (scanner.hasNextLine()) {
            line = scanner.nextLine()
            fileText.append(" ").append(line)
        }

        fileInputStream?.close()

        return ParsingResult.SuccessfullParsing(fileText.toString().trim())
    }
}
