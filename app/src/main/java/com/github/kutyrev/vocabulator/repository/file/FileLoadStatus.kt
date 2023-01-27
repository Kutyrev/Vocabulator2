package com.github.kutyrev.vocabulator.repository.file

import com.github.kutyrev.vocabulator.model.SubtitlesUnit

sealed class FileLoadStatus {
    class FileLoaded(val subtitles: SubtitlesUnit) : FileLoadStatus()
    class LoadingError(
        val error: FileLoadError,
        val lineNumber: Int? = null,
        val lineText: String? = null
    ) : FileLoadStatus()
}

enum class FileLoadError {
    UnsupportedFileExtension,
    FileFormatCorrupted
}
