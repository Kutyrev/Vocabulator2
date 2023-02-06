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
    subtitlesName: String?,
    onOrigWordChange: (String, WordCard) -> Unit,
    onTranslationChange: (String, WordCard) -> Unit
) {
    Scaffold(bottomBar = {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(modifier = Modifier.weight(WEIGHT_STD), onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.button_ok))
            }

            OutlinedButton(modifier = Modifier.weight(WEIGHT_STD), onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    }) {
        Column(modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_std))
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.padding_std)),
                textStyle = MaterialTheme.typography.body2,
                value = subtitlesName ?: "",
                onValueChange = { /*TODO*/ },
                label = { Text(stringResource(id = R.string.subtitles_name_label)) }
            )

            Button(onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.button_translate))
            }

            LazyColumn() {
                items(words) { word ->
                    Row {
                        Checkbox(checked = false, onCheckedChange = { /*TODO*/ })
                        TextField(
                            modifier = Modifier.weight(WEIGHT_STD),
                            textStyle = MaterialTheme.typography.caption,
                            value = word.originalWord,
                            onValueChange = { newValue -> onOrigWordChange(newValue, word) })
                        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
                        TextField(
                            modifier = Modifier.weight(WEIGHT_STD),
                            textStyle = MaterialTheme.typography.caption,
                            value = word.translatedWord,
                            onValueChange = { newValue -> onTranslationChange(newValue, word) })
                    }
                }
            }
        }
    }
}
