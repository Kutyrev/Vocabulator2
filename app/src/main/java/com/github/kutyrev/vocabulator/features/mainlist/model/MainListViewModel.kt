package com.github.kutyrev.vocabulator.features.mainlist.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainListViewModel @Inject constructor(private val storageRepository: StorageRepository) : ViewModel() {

    var subtitlesList: Flow<List<SubtitlesUnit>> = flowOf(listOf())

    fun initSubitlesListCollecting() {
        viewModelScope.launch {
            subtitlesList = storageRepository.getSubtitlesList()
        }
    }
}
