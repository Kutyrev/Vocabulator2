package com.github.kutyrev.vocabulator.features.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.features.settings.model.COUNT_OF_WORDS_INITIAL_VALUE
import com.github.kutyrev.vocabulator.features.settings.model.SettingsViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SettingsRoute(viewModel: SettingsViewModel = hiltViewModel(), onSaveSettings: () -> Unit) {
    LaunchedEffect(key1 = Unit) {
        viewModel.startCollectingNumberOfWordsForLoad()
    }
    val numberOfWords by
        viewModel.numberOfWordsForLoad.collectAsStateWithLifecycle(initialValue = COUNT_OF_WORDS_INITIAL_VALUE)

    Surface(modifier = Modifier.fillMaxSize()) {
        SettingsScreen(countOfWords = numberOfWords,
            onSaveButtonClick = viewModel::saveCountOfWordsForLoad,
            onSaveSettings = onSaveSettings,
            changeNumberOfWordsValue = viewModel::changeNumberOfWordsValue
        )
    }
}
