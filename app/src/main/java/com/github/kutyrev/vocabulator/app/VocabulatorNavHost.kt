package com.github.kutyrev.vocabulator.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

private const val MAIN_LIST_DESTINATION = "mainlist"

@Composable
fun VocabulatorNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MAIN_LIST_DESTINATION
) {
}
