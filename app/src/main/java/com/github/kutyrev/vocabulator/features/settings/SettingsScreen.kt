package com.github.kutyrev.vocabulator.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.github.kutyrev.vocabulator.R

@Composable
fun SettingsScreen(
    countOfWords: Int,
    isLoadPhrases: Boolean,
    onSaveButtonClick: () -> Unit,
    onSaveSettings: () -> Unit,
    changeNumberOfWordsValue: (Int) -> Unit,
    changeIsSetLoadPhrases: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
        TextField(
            label = { Text(stringResource(R.string.settings_number_of_words_label)) },
            value = countOfWords.toString(),
            onValueChange = { newText -> changeNumberOfWordsValue(newText.toInt()) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = isLoadPhrases, onCheckedChange = changeIsSetLoadPhrases)
            Text(
                text = stringResource(R.string.settings_load_phrases),
                style = MaterialTheme.typography.subtitle2
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
        Button(onClick = {
            onSaveButtonClick()
            onSaveSettings()
        }) {
            Text(stringResource(R.string.button_save))
        }
    }
}
