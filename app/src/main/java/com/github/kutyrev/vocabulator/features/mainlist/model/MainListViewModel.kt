package com.github.kutyrev.vocabulator.features.mainlist.model

import androidx.lifecycle.ViewModel
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MainListViewModel @Inject constructor(private val storageRepository: StorageRepository) : ViewModel() {

    val subtitlesList: Flow<List<SubtitlesUnit>> = flow {
        val source = storageRepository.getSubtitlesList()
        emitAll(source)
    }
}
