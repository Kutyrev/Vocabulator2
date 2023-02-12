package com.github.kutyrev.vocabulator.features.commons

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kutyrev.vocabulator.features.commons.model.CommonsViewModel

@Composable
fun CommonsRoute(
    viewModel: CommonsViewModel = hiltViewModel(),
    onOkButtonPressedRoute: () -> Unit,
    onCancelButtonPressed: () -> Unit,
) {
    Surface {
        CommonsScreen(
            language = viewModel.language.value,
            words = viewModel.words,
            searchText =viewModel.searchText.value,
            onLanguageChange = viewModel::onLanguageChange,
            onWordCheckedStateChange = viewModel::onWordCheckedStateChange,
            onSearchTextChange = viewModel::onSearchTextChange,
            onOkButtonPressed = viewModel::onOkButtonPressed,
            onOkButtonPressedRoute = onOkButtonPressedRoute,
            onCancelButtonPressed = onCancelButtonPressed
        )
    }
}
