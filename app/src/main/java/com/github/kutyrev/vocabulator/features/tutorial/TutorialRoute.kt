package com.github.kutyrev.vocabulator.features.tutorial

import androidx.compose.runtime.Composable

@Composable
fun TutorialRoute(onSettingsMenuItemClick: () -> Unit) {
    TutorialScreen(onSettingsMenuItemClick = onSettingsMenuItemClick)
}
