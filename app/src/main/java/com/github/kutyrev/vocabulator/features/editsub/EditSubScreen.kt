package com.github.kutyrev.vocabulator.features.editsub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.model.WordCard

private const val WEIGHT_STD = 1f

@Composable
fun EditSubScreen(
    words: List<WordCard>,
    onOrigWordChange: (String, WordCard) -> Unit,
    onTranslationChange: (String, WordCard) -> Unit
) {
    var text by remember { mutableStateOf("Hello") }

    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_std))) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_std)),
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(id = R.string.subtitles_name_label)) }
        )

        Button(onClick = { /*TODO*/ }) {
            Text(stringResource(R.string.button_translate))
        }

        LazyColumn(modifier = Modifier) {
            items(words) { word ->
                Row {
                    TextField(
                        modifier = Modifier.weight(WEIGHT_STD),
                        textStyle = MaterialTheme.typography.caption,
                        value = word.originalWord,
                        onValueChange = { newValue -> onOrigWordChange(newValue, word) })
                    TextField(
                        modifier = Modifier.weight(WEIGHT_STD),
                        textStyle = MaterialTheme.typography.caption,
                        value = word.translatedWord,
                        onValueChange = { newValue -> onTranslationChange(newValue, word) })
                }
            }
        }

        Row {
            OutlinedButton(modifier = Modifier.weight(WEIGHT_STD), onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.button_ok))
            }

            OutlinedButton(modifier = Modifier.weight(WEIGHT_STD), onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    }
}
