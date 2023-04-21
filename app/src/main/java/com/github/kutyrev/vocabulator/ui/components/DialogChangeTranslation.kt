package com.github.kutyrev.vocabulator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.github.kutyrev.vocabulator.R

const val TRANSLATION_DELIMITER = ";"
private const val WEIGHT_STD = 1f

@Composable
fun DialogChangeTranslation(
    checkableWords: MutableList<CheckableWord>,
    onChooseTranslation: (String) -> Unit,
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
            Column {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    checkableWords.forEach { curWord ->
                        Row {
                            Checkbox(
                                checked = curWord.checked,
                                onCheckedChange = { checked ->
                                    checkableWords[checkableWords.indexOf(curWord)] =
                                        checkableWords[checkableWords.indexOf(curWord)].copy(
                                            checked = checked
                                        )
                                })
                            TextField(
                                value = curWord.word,
                                textStyle = MaterialTheme.typography.subtitle2,
                                onValueChange = { newValue ->
                                    checkableWords[checkableWords.indexOf(curWord)] =
                                        checkableWords[checkableWords.indexOf(curWord)].copy(
                                            word = newValue
                                        )
                                })
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        modifier = Modifier.weight(WEIGHT_STD),
                        onClick = {
                            onChooseTranslation(checkableWords.joinToString(separator = "") {
                                if (it.checked && checkableWords.indexOf(it) != checkableWords.indices.last) it.word + ";"
                                else if (it.checked) it.word
                                else ""
                            })
                        }
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

data class CheckableWord(var checked: Boolean, var word: String)
