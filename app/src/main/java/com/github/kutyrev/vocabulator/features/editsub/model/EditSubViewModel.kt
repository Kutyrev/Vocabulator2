package com.github.kutyrev.vocabulator.features.editsub.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.kutyrev.vocabulator.app.LIST_ID_PARAM_NAME
import com.github.kutyrev.vocabulator.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditSubViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private var listId: Int = 0

    init {
        savedStateHandle.get<String>(LIST_ID_PARAM_NAME)?.let {
            listId = it.toInt()
        }
    }
}
