package com.github.kutyrev.vocabulator.features.editsub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard

private const val WEIGHT_STD = 1f

@Composable
fun EditSubScreen(
    words: List<WordCard>,
    subtitlesName: String?,
    origLanguage: Language,
    targetLanguage: Language,
    onOrigWordChange: (String, WordCard) -> Unit,
    onTranslationChange: (String, WordCard) -> Unit,
    onSubtitleNameChange: (String) ->  Unit,
    onSubtitlesLanguageChange: (Language) -> Unit,
    onTargetLanguageChange: (Language) -> Unit,
    onOkButtonPressed: () -> Unit
) {

    val languages = Language.values() /*TODO*/

    var origLanguageMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var targetLanguageMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(bottomBar = {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(modifier = Modifier.weight(WEIGHT_STD), onClick = onOkButtonPressed) {
                Text(stringResource(R.string.button_ok))
            }

            OutlinedButton(modifier = Modifier.weight(WEIGHT_STD), onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    }) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_short))
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.padding_std)),
                textStyle = MaterialTheme.typography.body2,
                value = subtitlesName ?: "",
                onValueChange = { newName -> onSubtitleNameChange(newName)},
                label = { Text(stringResource(id = R.string.subtitles_name_label)) }
            )

            Row {
                OutlinedButton(modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_std))
                    .weight(WEIGHT_STD),
                    onClick = { origLanguageMenuExpanded = true }) {
                    Text(origLanguage.name)
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.edit_scr_orig_lang_menu_desc)
                    )
                    DropdownMenu(
                        expanded = origLanguageMenuExpanded,
                        onDismissRequest = { origLanguageMenuExpanded = false }) {
                        for (language in languages) {
                            ClickableText(
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_std)),
                                text = AnnotatedString(language.name),
                                onClick = {
                                    origLanguageMenuExpanded = false
                                    onSubtitlesLanguageChange(language)
                                }
                            )
                        }
                    }
                }

                OutlinedButton(modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_std))
                    .weight(WEIGHT_STD),
                    onClick = { targetLanguageMenuExpanded = true }) {
                    Text(targetLanguage.name)
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.edit_scr_trans_lang_menu_desc)
                    )
                    DropdownMenu(
                        expanded = targetLanguageMenuExpanded,
                        onDismissRequest = { targetLanguageMenuExpanded = false }) {
                        for (language in languages) {
                            ClickableText(
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_std)),
                                text = AnnotatedString(language.name),
                                onClick = {
                                    targetLanguageMenuExpanded = false
                                    onTargetLanguageChange(language)
                                }
                            )
                        }
                    }
                }

            }

            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { /*TODO*/ }) {
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
