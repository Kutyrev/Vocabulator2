package com.github.kutyrev.vocabulator.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kutyrev.vocabulator.features.cards.CardsRoute
import com.github.kutyrev.vocabulator.features.editsub.EditSubRoute
import com.github.kutyrev.vocabulator.features.mainlist.MainListRoute

const val LIST_ID_PARAM_NAME = "listId"
private const val LIST_ID_PARAM_TEMPLATE = "{listId}"

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
            MainListRoute(onListItemClick = { listId ->
                navController.navigate(
                    VocabulatorDestinations.CardsDestinations.route.replace(
                        oldValue = LIST_ID_PARAM_TEMPLATE,
                        newValue = "$listId"
                    )
                )
            }, onEditButtonClick = { listId ->
                navController.navigate(
                    VocabulatorDestinations.EditSubtitlesDestination.route.replace(
                        oldValue = LIST_ID_PARAM_TEMPLATE,
                        newValue = "$listId"
                    )
                )
            })
        }
        composable(route = VocabulatorDestinations.CardsDestinations.route) {
            CardsRoute()
        }
        composable(route = VocabulatorDestinations.EditSubtitlesDestination.route) {
            EditSubRoute()
        }
    }
}
