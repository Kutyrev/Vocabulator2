package com.github.kutyrev.vocabulator.features.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kutyrev.vocabulator.model.WordCard

@Composable
fun CardsScreen(modifier: Modifier = Modifier, card: WordCard?, showTranslation: Boolean) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        card?.originalWord?.let { Text(it) }
        Spacer(modifier = Modifier.padding(8.dp))
        if (showTranslation) {
            card?.translatedWord?.let { Text(it) }
        }
    }
}
