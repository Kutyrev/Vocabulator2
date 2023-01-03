package com.github.kutyrev.vocabulator.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kutyrev.vocabulator.features.cards.CardsRoute
import com.github.kutyrev.vocabulator.features.mainlist.MainListRoute

@Composable
fun VocabulatorNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = VocabulatorDestinations.MainListDestination.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = VocabulatorDestinations.MainListDestination.route) {
            MainListRoute(onListItemClick = { navController.navigate(VocabulatorDestinations.CardsDestinations.route) })
        }
        composable(route = VocabulatorDestinations.CardsDestinations.route) {
            CardsRoute()
        }
    }
}

