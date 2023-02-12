package com.github.kutyrev.vocabulator.features.editsub

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kutyrev.vocabulator.features.editsub.model.EditSubViewModel

@Composable
fun EditSubRoute(
    viewModel: EditSubViewModel = hiltViewModel(),
    onOkButtonPressedRoute: () -> Unit,
    onCancelButtonPressed: () -> Unit
) {
    //val words = viewModel.words.collectAsStateWithLifecycle()

    Surface() {
        EditSubScreen(
            words = viewModel.words,
            subtitlesName = viewModel.subtitlesName.value,
            origLanguage = viewModel.subsLanguage.value,
            targetLanguage = viewModel.langOfTranslation.value,
            uncheckedToDict = viewModel.uncheckedToDict.value,
            onOrigWordChange = viewModel::onOrigWordChange,
            onTranslationChange = viewModel::onTranslationChange,
            onSubtitleNameChange = viewModel::onSubtitleNameChange,
            onSubtitlesLanguageChange = viewModel::onSubtitlesLanguageChange,
            onTargetLanguageChange = viewModel::onTargetLanguageChange,
            onWordCheckedStateChange = viewModel::onWordCheckedStateChange,
            onOkButtonPressed = viewModel::onOkButtonPressed,
            onOkButtonPressedRoute = onOkButtonPressedRoute,
            onCancelButtonPressed = onCancelButtonPressed,
            onChangeUncheckedToDict = viewModel::onChangeUncheckedToDict
        )
    }
}
