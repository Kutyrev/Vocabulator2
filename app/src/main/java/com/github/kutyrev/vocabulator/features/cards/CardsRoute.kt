package com.github.kutyrev.vocabulator.features.cards

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.features.cards.model.CardsViewModel
import com.github.kutyrev.vocabulator.model.EMPTY_CARD
import com.github.kutyrev.vocabulator.ui.components.DialogChangeTranslation

@Composable
fun CardsRoute(viewModel: CardsViewModel = hiltViewModel()) {
    val card = viewModel.card.collectAsStateWithLifecycle(initialValue = EMPTY_CARD)
    val messages = viewModel.messages.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(key1 = messages.value) {
        if (messages.value != null) {
            Toast.makeText(context, context.getText(messages.value!!.messageId), Toast.LENGTH_LONG)
                .show()
        }
    }

    if(viewModel.isEdit.value) {
        DialogChangeTranslation(
            checkableWords = viewModel.checkableWords,
            onChooseTranslation = viewModel::onChangeTranslation,
            onCancel = { viewModel.onIsEditStateChange(false) }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CardsScreen(
            card = card.value,
            isRandomCards = viewModel.isRandomCards.value,
            isForeignLangFirst = viewModel.isForeignLangFirst.value,
            showTranslation = viewModel.showTranslation.value,
            emitNewValue = viewModel::emitNewCard,
            onChangeIsRandomCardsState = viewModel::onChangeIsRandomCardsState,
            onNextCardButtonPressed = viewModel::onNextCardButtonPressed,
            onPreviousCardButtonPressed = viewModel::onPreviousCardButtonPressed,
            deleteWordCard = viewModel::deleteWordCard,
            addWordInCommons = viewModel::addWordInCommons,
            editTranslation = viewModel::onIsEditStateChange,
            onCardClick = viewModel::onCardClick
        )
    }
}
