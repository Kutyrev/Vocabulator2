package com.github.kutyrev.vocabulator.datasource.fileparsers

sealed class ParsingResult {
    class SuccessfullParsing(val parsedText: String) : ParsingResult()
    class InvalidTimestampFormatException(val lineNumber: Int, val line: String) : ParsingResult()
}
