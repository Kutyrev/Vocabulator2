package com.github.kutyrev.vocabulator.features.mainlist

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.features.mainlist.model.MainListViewModel
import com.github.kutyrev.vocabulator.model.EMPTY_SUBS_ID
import com.github.kutyrev.vocabulator.repository.file.FileLoadStatus
import com.github.kutyrev.vocabulator.ui.components.DialogBoxLoading
import com.github.kutyrev.vocabulator.ui.theme.VocabulatorTheme

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainListRoute(
    viewModel: MainListViewModel = hiltViewModel(),
    onListItemClick: (Int) -> Unit,
    onEditButtonClick: (Int) -> Unit,
    onSettingsMenuItemClick: () -> Unit,
    onCommonsButtonClick: () -> Unit
) {
    val listState = viewModel.subtitlesList.collectAsStateWithLifecycle(initialValue = listOf())

    val fileLoadStatus by viewModel.fileLoadingStatus

    val context = LocalContext.current

    LaunchedEffect(key1 = fileLoadStatus) {

        if (fileLoadStatus is FileLoadStatus.FileLoaded) {
            if (viewModel.newSubsId.value != EMPTY_SUBS_ID) {
                onEditButtonClick(viewModel.newSubsId.value)
            }
        }
        if (fileLoadStatus is FileLoadStatus.LoadingError) {
            Toast.makeText(
                context,
                context.getText((fileLoadStatus as FileLoadStatus.LoadingError).error.messageRes),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    if (viewModel.showLoadingDialog.value) {
        DialogBoxLoading()
    }

    VocabulatorTheme {
        MainStructureScreen(
            listState = listState,
            unswipedSubtitleUnit = viewModel.unswipedSubtitlesUnit.value,
            onListItemClick = onListItemClick,
            onEditButtonClick = onEditButtonClick,
            onSettingsMenuItemClick = onSettingsMenuItemClick,
            onCommonsButtonClick = onCommonsButtonClick,
            checkFileExtension = viewModel::checkFileExtension,
            setLanguage = viewModel::setSubsLanguage,
            loadFile = viewModel::parseFile,
            onSubtitleSwiped = viewModel::onSubtitleSwiped,
            setUnswipedSubtitleUnit = viewModel::setUnswipedSubtitleUnit
        )
    }
}
