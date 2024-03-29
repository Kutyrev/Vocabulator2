package com.github.kutyrev.vocabulator.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kutyrev.vocabulator.features.about.AboutRoute
import com.github.kutyrev.vocabulator.features.cards.CardsRoute
import com.github.kutyrev.vocabulator.features.commons.CommonsRoute
import com.github.kutyrev.vocabulator.features.editsub.EditSubRoute
import com.github.kutyrev.vocabulator.features.mainlist.MainListRoute
import com.github.kutyrev.vocabulator.features.settings.SettingsRoute
import com.github.kutyrev.vocabulator.features.tutorial.TutorialRoute

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
            },
                onSettingsMenuItemClick = {
                    navController.navigate(VocabulatorDestinations.SettingsDestination.route)
                },
                onCommonsButtonClick = { navController.navigate(VocabulatorDestinations.CommonsDestination.route) },
                onAboutButtonClick = { navController.navigate(VocabulatorDestinations.AboutDestination.route) },
                onTutorialButtonClick = { navController.navigate(VocabulatorDestinations.TutorialDestination.route) }
            )
        }
        composable(route = VocabulatorDestinations.CardsDestinations.route) {
            CardsRoute()
        }
        composable(route = VocabulatorDestinations.EditSubtitlesDestination.route) {
            EditSubRoute(
                onOkButtonPressedRoute = { navController.popBackStack() },
                onCancelButtonPressed = { navController.popBackStack() })
        }
        composable(route = VocabulatorDestinations.SettingsDestination.route) {
            SettingsRoute(onSaveSettings = { navController.popBackStack() })
        }
        composable(route = VocabulatorDestinations.CommonsDestination.route) {
            CommonsRoute(
                onOkButtonPressedRoute = { navController.popBackStack() },
                onCancelButtonPressed = { navController.popBackStack() })
        }
        composable(route = VocabulatorDestinations.AboutDestination.route) {
            AboutRoute()
        }
        composable(route = VocabulatorDestinations.TutorialDestination.route) {
            TutorialRoute(onSettingsMenuItemClick = {
                navController.popBackStack()
                navController.navigate(VocabulatorDestinations.SettingsDestination.route)
            }, onCloseButtonClick = { navController.popBackStack() })
        }
    }
}
