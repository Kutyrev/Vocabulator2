package com.github.kutyrev.vocabulator.features.editsub

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.features.editsub.model.EditSubViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun EditSubRoute(viewModel: EditSubViewModel = hiltViewModel()) {
    val words = viewModel.words.collectAsStateWithLifecycle()

    Surface() {
        EditSubScreen(
            words = words.value,
            subtitlesName = viewModel.subtitlesUnit.value?.name,
            onOrigWordChange = viewModel::onOrigWordChange,
            onTranslationChange = viewModel::onTranslationChange
        )
    }
}
