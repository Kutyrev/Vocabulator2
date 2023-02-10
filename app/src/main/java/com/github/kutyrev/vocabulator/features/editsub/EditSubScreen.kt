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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.features.editsub.model.EditableWordCard
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard

private const val WEIGHT_STD = 1f

@Composable
fun EditSubScreen(
    words: List<EditableWordCard>,
    subtitlesName: String?,
    origLanguage: Language,
    targetLanguage: Language,
    uncheckedToDict: Boolean,
    onOrigWordChange: (String, WordCard) -> Unit,
    onTranslationChange: (String, WordCard) -> Unit,
    onSubtitleNameChange: (String) -> Unit,
    onSubtitlesLanguageChange: (Language) -> Unit,
    onTargetLanguageChange: (Language) -> Unit,
    onWordCheckedStateChange: (EditableWordCard, Boolean) -> Unit,
    onOkButtonPressed: () -> Unit,
    onCancelButtonPressed: () -> Unit,
    onChangeUncheckedToDict: (Boolean) -> Unit
) {
    val languages = remember { Language.values() }

    Scaffold(
        topBar = {
            TopBar(
                subtitlesName = subtitlesName,
                onSubtitleNameChange = onSubtitleNameChange,
                origLanguage = origLanguage,
                languages = languages,
                onSubtitlesLanguageChange = onSubtitlesLanguageChange,
                targetLanguage = targetLanguage,
                onTargetLanguageChange = onTargetLanguageChange,
                uncheckedToDict = uncheckedToDict,
                onChangeUncheckedToDict = onChangeUncheckedToDict
            )
        },
        bottomBar = { BottomBar(
            onOkButtonPressed = onOkButtonPressed,
            onCancelButtonPressed = onCancelButtonPressed
        ) }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(words) { word ->
                Row {
                    Checkbox(
                        checked = word.checked,
                        onCheckedChange = { checked -> onWordCheckedStateChange(word, checked) })
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

@Composable
private fun BottomBar(
    onOkButtonPressed: () -> Unit,
    onCancelButtonPressed: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            modifier = Modifier.weight(WEIGHT_STD),
            onClick = onOkButtonPressed
        ) {
            Text(stringResource(R.string.button_ok))
        }

        OutlinedButton(
            modifier = Modifier.weight(WEIGHT_STD),
            onClick = onCancelButtonPressed
        ) {
            Text(stringResource(R.string.button_cancel))
        }
    }
}

@Composable
private fun TopBar(
    subtitlesName: String?,
    onSubtitleNameChange: (String) -> Unit,
    origLanguage: Language,
    languages: Array<Language>,
    onSubtitlesLanguageChange: (Language) -> Unit,
    targetLanguage: Language,
    onTargetLanguageChange: (Language) -> Unit,
    uncheckedToDict: Boolean,
    onChangeUncheckedToDict: (Boolean) -> Unit
) {
    var origLanguageMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var targetLanguageMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_short))
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_std)),
            textStyle = MaterialTheme.typography.body2,
            value = subtitlesName ?: "",
            onValueChange = { newName -> onSubtitleNameChange(newName) },
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = uncheckedToDict,
                onCheckedChange = { onChangeUncheckedToDict(it) })

            Text(
                text = stringResource(R.string.add_unchecked_text),
                style = MaterialTheme.typography.caption
            )
        }
    }
}
