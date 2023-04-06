package com.github.kutyrev.vocabulator.features.editsub

import android.widget.Toast
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.features.editsub.model.EditSubViewModel
import com.github.kutyrev.vocabulator.ui.components.DialogBoxLoading
import com.github.kutyrev.vocabulator.ui.components.DialogChangeTranslation
import com.github.kutyrev.vocabulator.ui.components.DialogNewWordCard

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun EditSubRoute(
    viewModel: EditSubViewModel = hiltViewModel(),
    onOkButtonPressedRoute: () -> Unit,
    onCancelButtonPressed: () -> Unit
) {
    //val words = viewModel.words.collectAsStateWithLifecycle()
    val messages = viewModel.messages.collectAsStateWithLifecycle()

    val context = LocalContext.current

    if (viewModel.showLoadingDialog.value) {
        DialogBoxLoading()
    }

    if (viewModel.showAddNewWordCardDialog.value) {
        DialogNewWordCard(
            createWordCard = viewModel::addNewWordCard,
            onCancel = { viewModel.setAddNewWordCardDialogVisibility(false) })
    }

    LaunchedEffect(key1 = messages.value) {
        if (messages.value != null) {
            Toast.makeText(context, context.getText(messages.value!!.messageId), Toast.LENGTH_LONG)
                .show()
        }
    }

    if (viewModel.isEdit.value) {
        DialogChangeTranslation(
            checkableWords = viewModel.checkableWords,
            onChooseTranslation = viewModel::onChangeTranslation,
            onCancel = { viewModel.onIsEditStateChange(false) }
        )
    }

    Surface() {
        EditSubScreen(
            words = viewModel.words,
            subtitlesName = viewModel.subtitlesName.value,
            origLanguage = viewModel.subsLanguage.value,
            targetLanguage = viewModel.langOfTranslation.value,
            uncheckedToDict = viewModel.uncheckedToDict.value,
            isFirstLoad = viewModel.isFirstLoad,
            onOrigWordChange = viewModel::onOrigWordChange,
            onTranslationChange = viewModel::onTranslationChange,
            onSubtitleNameChange = viewModel::onSubtitleNameChange,
            onSubtitlesLanguageChange = viewModel::onSubtitlesLanguageChange,
            onTargetLanguageChange = viewModel::onTargetLanguageChange,
            onWordCheckedStateChange = viewModel::onWordCheckedStateChange,
            onTranslateButtonClicked = viewModel::translateWords,
            onOkButtonPressed = viewModel::onOkButtonPressed,
            onOkButtonPressedRoute = onOkButtonPressedRoute,
            onCancelButtonPressed = onCancelButtonPressed,
            onChangeUncheckedToDict = viewModel::onChangeUncheckedToDict,
            onTranslationClick = viewModel::onTranslationClick,
            updateCommonsAndReloadFile = viewModel::updateCommonsAndReloadFile,
            setAddNewWordCardDialogVisibility = viewModel::setAddNewWordCardDialogVisibility
        )
    }
}
