package com.github.kutyrev.vocabulator.repository.file

import android.net.Uri
import com.github.kutyrev.vocabulator.model.Language

interface FileRepository {
    suspend fun parseFile(uri: Uri, language: Language, fileName: String) : FileLoadStatus
}
