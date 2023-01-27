package com.github.kutyrev.vocabulator.features.mainlist.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MainListViewModel @Inject constructor(private val storageRepository: StorageRepository) : ViewModel() {

    private val fileExtensionRegExPattern = "^.*\\.(srt|SRT|ssa|SSA|ass|ASS|txt|TXT|fb2|FB2)\$".toRegex()
    private var subsLanguage: Language? = null

    val subtitlesList: Flow<List<SubtitlesUnit>> = flow {
        val source = storageRepository.getSubtitlesList()
        emitAll(source)
    }

    fun checkFileExtension(uriString: String): Boolean {
        return fileExtensionRegExPattern.matches(uriString)
    }

    fun setSubsLanguage(language: Language?) {
        subsLanguage = language
    }

    fun parseFile(uri: Uri, fileName: String?) {


    }

}

