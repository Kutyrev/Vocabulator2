package com.github.kutyrev.vocabulator.repository.file

import android.net.Uri
import com.github.kutyrev.vocabulator.features.editsub.model.EditableWordCard
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard

interface FileRepository {
    var sortedWords: Map<String, Int>
    suspend fun parseFile(uri: Uri, language: Language, fileName: String): FileLoadStatus
    suspend fun reparseSubtitles(subtitlesUnit: SubtitlesUnit): List<WordCard>
    suspend fun exportSubtitles(uri: Uri, words: List<EditableWordCard>): FileExportStatus
}
