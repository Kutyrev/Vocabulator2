package com.github.kutyrev.vocabulator.features.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.model.WordCard
import kotlin.math.roundToInt

private const val WEIGHT_STD = 1f

@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    card: WordCard,
    isRandomCards: Boolean,
    isForeignLangFirst: Boolean,
    emitNewValue: (Float) -> Unit,
    onChangeIsRandomCardsState: (Boolean) -> Unit,
    onNextCardButtonPressed: () -> Unit,
    onPreviousCardButtonPressed: () -> Unit
) {
    var showTranslation by remember {
        mutableStateOf(false)
    }

    var offsetX by remember { mutableStateOf(0f) }

    Scaffold(modifier = Modifier, topBar = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isRandomCards, onCheckedChange = onChangeIsRandomCardsState)
            Text(
                text = stringResource(R.string.cards_scr_random_cards_text),
                style = MaterialTheme.typography.caption
            )
        }
    }, bottomBar = {
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
    }) {
        Card(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .clickable {
                showTranslation = !showTranslation
            }
            .offset { IntOffset((offsetX).roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(onDragEnd = {
                    emitNewValue(offsetX)
                    offsetX = 0f
                    showTranslation = false
                }, onDragCancel = { offsetX = 0f }, onHorizontalDrag = { change, dragAmount ->
                    offsetX += dragAmount
                })
            }) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isForeignLangFirst || (isForeignLangFirst && showTranslation)) {
                    card.originalWord.let { originalWord -> Text(originalWord) }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                if ((isForeignLangFirst && showTranslation) || !isForeignLangFirst) {
                    card.translatedWord.let { translatedWord -> Text(translatedWord) }
                }
            }
        }
    }
}
