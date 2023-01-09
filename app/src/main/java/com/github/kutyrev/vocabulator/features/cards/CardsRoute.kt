package com.github.kutyrev.vocabulator.features.cards

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.features.cards.model.CardsViewModel
import com.github.kutyrev.vocabulator.model.EMPTY_CARD

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun CardsRoute(viewModel: CardsViewModel = hiltViewModel()) {
    val card = viewModel.card.collectAsStateWithLifecycle(initialValue = EMPTY_CARD)

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CardsScreen(card = card.value, emitNewValue = viewModel::emitNewCard)
    }
}
