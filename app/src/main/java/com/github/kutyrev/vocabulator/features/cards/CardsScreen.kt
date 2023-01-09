package com.github.kutyrev.vocabulator.features.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.kutyrev.vocabulator.model.WordCard
import kotlin.math.roundToInt

@Composable
fun CardsScreen(modifier: Modifier = Modifier, card: WordCard, emitNewValue: (Float) -> Unit) {
    var showTranslation by remember {
        mutableStateOf(false)
    }

    var offsetX by remember { mutableStateOf(0f) }

    Card(modifier = Modifier
        .fillMaxSize()
        .clickable {
            showTranslation = !showTranslation
        }
        .offset { IntOffset((offsetX).roundToInt(), 0) }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    emitNewValue(offsetX)
                    offsetX = 0f
                    showTranslation = false
                },
                onDragCancel = { offsetX = 0f },
                onHorizontalDrag = { change, dragAmount ->
                    offsetX += dragAmount
                })
        }) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            card.originalWord.let { Text(it) }
            Spacer(modifier = Modifier.padding(8.dp))
            if (showTranslation) {
                card.translatedWord.let { Text(it) }
            }
        }
    }
}
