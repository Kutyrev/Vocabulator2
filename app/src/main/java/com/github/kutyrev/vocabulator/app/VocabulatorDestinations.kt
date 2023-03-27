package com.github.kutyrev.vocabulator.app

enum class VocabulatorDestinations(val route: String) {
    MainListDestination("mainlist"),
    CardsDestinations("cards/{listId}"),
    EditSubtitlesDestination("edit/{listId}"),
    SettingsDestination("settings"),
    CommonsDestination("commons"),
    AboutDestination("about")
}
