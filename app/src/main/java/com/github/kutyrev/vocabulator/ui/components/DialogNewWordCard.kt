package com.github.kutyrev.vocabulator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.github.kutyrev.vocabulator.R

private const val WEIGHT_STD = 1f

@Composable
fun DialogNewWordCard(
    createWordCard: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)),
            elevation = dimensionResource(id = R.dimen.elevation_std),
            border = BorderStroke(
                dimensionResource(id = R.dimen.edit_dialog_border_stroke),
                MaterialTheme.colors.surface
            )
        ) {
            var origWord by rememberSaveable { mutableStateOf("") }
            var translatedWord by rememberSaveable { mutableStateOf("") }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    value = origWord,
                    onValueChange = { value -> origWord = value },
                    label = { Text(text = stringResource(id = R.string.add_word_dialog_orig)) }
                )
                Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
                TextField(
                    value = translatedWord,
                    onValueChange = { value -> translatedWord = value },
                    label = { Text(text = stringResource(id = R.string.add_word_dialog_trans)) })
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        modifier = Modifier.weight(WEIGHT_STD),
                        onClick = { createWordCard(origWord, translatedWord) }
                    ) {
                        Text(stringResource(R.string.button_ok))
                    }

                    OutlinedButton(
                        modifier = Modifier.weight(WEIGHT_STD),
                        onClick = onCancel
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                }
            }
        }
    }
}
