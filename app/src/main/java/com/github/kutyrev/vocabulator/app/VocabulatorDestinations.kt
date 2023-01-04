package com.github.kutyrev.vocabulator.app

enum class VocabulatorDestinations(val route: String) {
    MainListDestination("mainlist"),
    CardsDestinations("cards/{listId}")
}
