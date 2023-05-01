package com.github.kutyrev.vocabulator.features.mainlist.model

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.model.EMPTY_SUBS_ID
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.repository.datastore.SettingsRepository
import com.github.kutyrev.vocabulator.repository.storage.StorageRepository
import com.github.kutyrev.vocabulator.repository.file.FileLoadStatus
import com.github.kutyrev.vocabulator.repository.file.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainListViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val fileRepository: FileRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val fileExtensionRegExPattern =
        "^.*\\.(srt|SRT|ssa|SSA|ass|ASS|txt|TXT|fb2|FB2)\$".toRegex()
    private var subsLanguage: Language? = null

    val subtitlesList: Flow<List<SubtitlesUnit>> = flow {
        val source = storageRepository.getSubtitlesList()
        emitAll(source)
    }

    private var _fileLoadingStatus: MutableState<FileLoadStatus> =
        mutableStateOf(FileLoadStatus.None)
    val fileLoadingStatus: MutableState<FileLoadStatus>
        get() = _fileLoadingStatus

    private var _showLoadingDialog: MutableState<Boolean> = mutableStateOf(false)
    val showLoadingDialog: MutableState<Boolean>
        get() = _showLoadingDialog

    private var _newSubsId: MutableState<Int> = mutableStateOf(EMPTY_SUBS_ID)
    val newSubsId: MutableState<Int>
        get() = _newSubsId

    var unswipedSubtitlesUnit: MutableState<SubtitlesUnit?> = mutableStateOf(null)
        private set

    var showTutorial : MutableState<Boolean> = mutableStateOf(false)
        private set

    fun checkFileExtension(uriString: String): Boolean {
        return fileExtensionRegExPattern.matches(uriString)
    }

    fun setSubsLanguage(language: Language?) {
        subsLanguage = language
    }

    fun parseFile(uri: Uri, fileName: String?) {
        _fileLoadingStatus.value = FileLoadStatus.Loading
        _showLoadingDialog.value = true
        viewModelScope.launch {
            subsLanguage?.let {
                if (fileName != null) {
                    val localFileLoadingStatus =
                        fileRepository.parseFile(uri = uri, language = it, fileName = fileName)
                    _showLoadingDialog.value = false
                    if (localFileLoadingStatus is FileLoadStatus.FileLoaded) {
                        _newSubsId.value =
                            storageRepository.insertNewSubtitles(localFileLoadingStatus.subtitles)
                        _fileLoadingStatus.value = localFileLoadingStatus
                    }
                }
            }
        }
    }

    fun onSubtitleSwiped(subtitlesUnit: SubtitlesUnit) {
        viewModelScope.launch {
            storageRepository.deleteSubtitles(subtitlesUnit)
        }
    }

    fun setUnswipedSubtitleUnit(newValue: SubtitlesUnit?) {
        unswipedSubtitlesUnit.value = newValue
    }

    fun resetLoadingStatus() {
        _fileLoadingStatus.value = FileLoadStatus.None
        _newSubsId.value = EMPTY_SUBS_ID
    }

 fun checkIsFirstRun() {
        viewModelScope.launch {
            if(settingsRepository.getIsFirstRun().first()) {
                settingsRepository.setIsFirstRun(false)
                showTutorial.value = true
            }
        }
    }

    fun resetShowTutorialStatus() {
        showTutorial.value = false
    }
}
