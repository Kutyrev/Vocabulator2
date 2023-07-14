package com.github.kutyrev.vocabulator.features.commons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.features.commons.model.EditableCommonWord
import com.github.kutyrev.vocabulator.model.Language
import kotlinx.coroutines.launch

private const val WEIGHT_STD = 1f

@Composable
fun CommonsScreen(
    language: Language,
    words: List<EditableCommonWord>,
    searchText: String,
    onLanguageChange: (Language) -> Unit,
    onWordCheckedStateChange: (EditableCommonWord, Boolean) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onOkButtonPressed: () -> Unit,
    onOkButtonPressedRoute: () -> Unit,
    onCancelButtonPressed: () -> Unit
) {

    val languages = remember { Language.values() }
    val listState = rememberLazyListState()

    Scaffold(topBar = {
        TopBar(
            language = language,
            languages = languages,
            listState = listState,
            words = words,
            searchText = searchText,
            onLanguageChange = onLanguageChange,
            onSearchTextChange = onSearchTextChange
        )
    }, bottomBar = {
        BottomBar(
            onOkButtonPressed = onOkButtonPressed,
            onOkButtonPressedRoute = onOkButtonPressedRoute,
            onCancelButtonPressed = onCancelButtonPressed
        )
    }) { paddingValues ->
        val columnDesc = stringResource(R.string.commons_scr_col_desc)

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    contentDescription = columnDesc
                }, state = listState
        ) {
            items(words) { commonWord ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = commonWord.checked, onCheckedChange = { checked ->
                        onWordCheckedStateChange(
                            commonWord, checked
                        )
                    })
                    Text(
                        text = commonWord.word,
                        style = MaterialTheme.typography.subtitle2,
                        textDecoration = if (commonWord.checked) TextDecoration.None
                        else TextDecoration.LineThrough
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    onOkButtonPressed: () -> Unit,
    onOkButtonPressedRoute: () -> Unit,
    onCancelButtonPressed: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(modifier = Modifier.weight(WEIGHT_STD),
            onClick = {
                onOkButtonPressed()
                onOkButtonPressedRoute()
            }) {
            Text(stringResource(R.string.button_ok))
        }
        OutlinedButton(
            modifier = Modifier.weight(WEIGHT_STD), onClick = onCancelButtonPressed
        ) {
            Text(stringResource(R.string.button_cancel))
        }
    }
}

@Composable
private fun TopBar(
    language: Language,
    languages: Array<Language>,
    listState: LazyListState,
    words: List<EditableCommonWord>,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onLanguageChange: (Language) -> Unit
) {
    var languageMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    Row {
        OutlinedButton(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)),
            onClick = { languageMenuExpanded = true }) {
            Text(language.name)
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.commons_scr_orig_lang_menu_desc)
            )
            DropdownMenu(expanded = languageMenuExpanded,
                onDismissRequest = { languageMenuExpanded = false }) {
                for (curLanguage in languages) {
                    ClickableText(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)),
                        text = AnnotatedString(
                            curLanguage.name
                                    + " " + stringResource(id = curLanguage.fullNameResource)
                        ),
                        style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.onBackground),
                        onClick = {
                            languageMenuExpanded = false
                            onLanguageChange(curLanguage)
                        })
                }
            }
        }

        TextField(
            value = searchText,
            singleLine = true,
            textStyle = MaterialTheme.typography.subtitle2,
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.commons_scr_search)
                )
            },
            onValueChange = { newText ->
                onSearchTextChange(newText)
                coroutineScope.launch {
                    val foundedWord = words.find {
                        it.word.startsWith(newText)
                    }
                    if (foundedWord != null) {
                        listState.animateScrollToItem(words.indexOf(foundedWord))
                    }
                }
            })
    }
}
