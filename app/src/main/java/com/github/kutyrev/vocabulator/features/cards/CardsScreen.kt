package com.github.kutyrev.vocabulator.features.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.model.WordCard
import kotlin.math.roundToInt

private const val WEIGHT_STD = 1f
private const val INIT_OFFSET = 0f

@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    card: WordCard,
    isRandomCards: Boolean,
    isForeignLangFirst: Boolean,
    showTranslation: Boolean,
    emitNewValue: (Float) -> Unit,
    onChangeIsRandomCardsState: (Boolean) -> Unit,
    onNextCardButtonPressed: () -> Unit,
    onPreviousCardButtonPressed: () -> Unit,
    deleteWordCard: () -> Unit,
    addWordInCommons: () -> Unit,
    editTranslation: (Boolean) -> Unit,
    onCardClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(INIT_OFFSET) }

    Scaffold(modifier = Modifier, topBar = {
        CardsTopAppBar(
            addWordInCommons,
            isRandomCards,
            onChangeIsRandomCardsState,
            deleteWordCard,
            editTranslation
        )
    }, bottomBar = {
        CardsBottomBar(onNextCardButtonPressed, onPreviousCardButtonPressed)
    }) {
        Card(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .clickable {
                onCardClick()
            }
            .offset { IntOffset((offsetX).roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        emitNewValue(offsetX)
                        offsetX = INIT_OFFSET
                    },
                    onDragCancel = { offsetX = INIT_OFFSET },
                    onHorizontalDrag = { change, dragAmount ->
                        offsetX += dragAmount
                    })
            }) {
            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isForeignLangFirst || showTranslation) {
                    card.originalWord.let { originalWord -> Text(originalWord) }
                }
                Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
                if ((isForeignLangFirst && showTranslation) || !isForeignLangFirst) {
                    card.translatedWord.let { translatedWord -> Text(translatedWord) }
                }
            }
        }
    }
}

@Composable
private fun CardsBottomBar(
    onNextCardButtonPressed: () -> Unit,
    onPreviousCardButtonPressed: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            modifier = Modifier.weight(WEIGHT_STD), onClick = onNextCardButtonPressed
        ) {
            Text(stringResource(R.string.cards_scr_prev_card_button))
        }

        OutlinedButton(
            modifier = Modifier.weight(WEIGHT_STD), onClick = onPreviousCardButtonPressed
        ) {
            Text(stringResource(R.string.cards_scr_next_card_button))
        }
    }
}

@Composable
private fun CardsTopAppBar(
    addWordInCommons: () -> Unit,
    isRandomCards: Boolean,
    onChangeIsRandomCardsState: (Boolean) -> Unit,
    deleteWordCard: () -> Unit,
    editTranslation: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = addWordInCommons) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_fact_check_24),
                contentDescription = stringResource(R.string.cards_scr_add_in_commons_button)
            )
        }
        Spacer(modifier = Modifier.weight(WEIGHT_STD))
        Checkbox(checked = isRandomCards, onCheckedChange = onChangeIsRandomCardsState)
        Text(
            text = stringResource(R.string.cards_scr_random_cards_text),
            style = MaterialTheme.typography.caption
        )
        Spacer(modifier = Modifier.weight(WEIGHT_STD))
        IconButton(onClick = { editTranslation(true) }) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.cards_scr_edit_button)
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
        IconButton(onClick = deleteWordCard) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.cards_scr_delete_word_button)
            )
        }
    }
}
