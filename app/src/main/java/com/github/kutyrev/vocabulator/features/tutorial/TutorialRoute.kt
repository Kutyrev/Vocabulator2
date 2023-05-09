package com.github.kutyrev.vocabulator.features.tutorial

import androidx.compose.runtime.Composable

@Composable
fun TutorialRoute(onSettingsMenuItemClick: () -> Unit, onCloseButtonClick: () -> Unit) {
    TutorialScreen(
        onSettingsMenuItemClick = onSettingsMenuItemClick,
        onCloseButtonClick = onCloseButtonClick
    )
}
