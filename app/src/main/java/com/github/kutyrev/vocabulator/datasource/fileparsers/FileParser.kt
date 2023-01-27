package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.net.Uri

interface FileParser {
    fun parseFile(uri: Uri): ParsingResult
}
