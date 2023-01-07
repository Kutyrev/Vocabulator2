package com.github.kutyrev.vocabulator.features.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.features.cards.model.CardsViewModel
import com.github.kutyrev.vocabulator.model.WordCard

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun CardsRoute(viewModel: CardsViewModel = hiltViewModel()) {
    val cards = viewModel.cards.collectAsStateWithLifecycle(initialValue = listOf())

    val cardIndex = rememberSaveable {
        0
    }

    var card: WordCard? = null

    if (cards.value.isNotEmpty()) card = cards.value[cardIndex]

    var showTranslation by remember {
        mutableStateOf(false)
    }

    Surface(modifier = Modifier
        .fillMaxSize()
        .clickable {
            showTranslation = !showTranslation
        }) {
        CardsScreen(card = card, showTranslation = showTranslation)
    }
}
