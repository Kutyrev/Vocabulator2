package com.github.kutyrev.vocabulator.datasource.fileparsers

import com.github.kutyrev.vocabulator.model.SupportedFileExtension

interface ParserFactory {
    fun getParser(fileExtension: SupportedFileExtension) : FileParser?
}
