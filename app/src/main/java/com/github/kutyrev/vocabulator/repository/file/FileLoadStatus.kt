package com.github.kutyrev.vocabulator.repository.file

import androidx.annotation.StringRes
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.model.SubtitlesUnit

sealed class FileLoadStatus {
    object Loading : FileLoadStatus()
    class FileLoaded(val subtitles: SubtitlesUnit) : FileLoadStatus()
    class LoadingError(
        val error: FileLoadError,
        val lineNumber: Int? = null,
        val lineText: String? = null
    ) : FileLoadStatus()
    object None : FileLoadStatus()
}

enum class FileLoadError(@StringRes val messageRes: Int) {
    UnsupportedFileExtension(R.string.toast_unsupported_file),
    FileFormatCorrupted(R.string.toast_file_format_corrupted),
    IOException(R.string.toast_io_exception)
}
