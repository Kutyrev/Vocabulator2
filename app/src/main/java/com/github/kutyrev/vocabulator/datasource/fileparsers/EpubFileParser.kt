package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.Context
import android.net.Uri
import java.io.InputStream
import java.util.Scanner
import java.util.zip.ZipInputStream

private const val XHTML_RES = "xhtml"
private const val TOC_XHTML = "toc.xhtml"
private const val COVER_XHTML = "cover.xhtml"
private const val CLEAN_TAGS_REGEX = "<.*?>"

class EpubFileParser(private val context: Context) : FileParser {

    override fun parseFile(uri: Uri): ParsingResult {
        val fileInputStream: InputStream? = context.contentResolver.openInputStream(uri)

        val epubFile = ZipInputStream(fileInputStream)

        val entryNames : MutableList<String> = mutableListOf()

        var result = ""

        var curEntry = epubFile.nextEntry
        while (curEntry != null) {
            if(!curEntry.isDirectory
                && curEntry.name.endsWith(XHTML_RES, true)
                && !curEntry.name.endsWith(TOC_XHTML, true)
                && !curEntry.name.endsWith(COVER_XHTML, true)
                ) {
                val entryName: String = curEntry.name
                entryNames.add(entryName)

                val scanner = Scanner(epubFile)
                while (scanner.hasNextLine()) {
                    result += scanner.nextLine()
                }
            }
            curEntry = epubFile.nextEntry
        }

        val parsedResult = result.replace(Regex(CLEAN_TAGS_REGEX), "")

        fileInputStream?.close()

        return ParsingResult.SuccessfullParsing(parsedText = parsedResult.trim())
    }
}
