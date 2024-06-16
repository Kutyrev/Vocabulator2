package com.github.kutyrev.vocabulator.features.editsub

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.features.editsub.model.EditableWordCard
import com.github.kutyrev.vocabulator.model.Language

private const val WEIGHT_STD = 1f
private const val HALF_WEIGHT = 0.5f
private const val CSV_FILE_MIME_TYPE = "text/csv"

@Composable
fun EditSubScreen(
    words: List<EditableWordCard>,
    subtitlesName: String?,
    origLanguage: Language,
    targetLanguage: Language,
    uncheckedToDict: Boolean,
    isFirstLoad: Boolean,
    onOrigWordChange: (String, EditableWordCard) -> Unit,
    onTranslationChange: (String, EditableWordCard) -> Unit,
    onSubtitleNameChange: (String) -> Unit,
    onSubtitlesLanguageChange: (Language) -> Unit,
    onTargetLanguageChange: (Language) -> Unit,
    onWordCheckedStateChange: (EditableWordCard, Boolean) -> Unit,
    onTranslateButtonClicked: () -> Unit,
    onOkButtonPressed: () -> Unit,
    onOkButtonPressedRoute: () -> Unit,
    onCancelButtonPressed: () -> Unit,
    onChangeUncheckedToDict: (Boolean) -> Unit,
    onTranslationClick: (EditableWordCard) -> Unit,
    updateCommonsAndReloadFile: () -> Unit,
    setAddNewWordCardDialogVisibility: (Boolean) -> Unit,
    saveListToCsvFile: (Uri) -> Unit
) {
    val languages = remember { Language.entries.toTypedArray() }

    Scaffold(
        topBar = {
            TopBar(
                subtitlesName = subtitlesName,
                isFirstLoad = isFirstLoad,
                onSubtitleNameChange = onSubtitleNameChange,
                origLanguage = origLanguage,
                languages = languages,
                onSubtitlesLanguageChange = onSubtitlesLanguageChange,
                targetLanguage = targetLanguage,
                onTargetLanguageChange = onTargetLanguageChange,
                uncheckedToDict = uncheckedToDict,
                onChangeUncheckedToDict = onChangeUncheckedToDict,
                onTranslateButtonClicked = onTranslateButtonClicked,
                updateCommonsAndReloadFile = updateCommonsAndReloadFile,
                saveListToCsvFile = saveListToCsvFile
            )
        },
        bottomBar = {
            BottomBar(
                onOkButtonPressed = onOkButtonPressed,
                onOkButtonPressedRoute = onOkButtonPressedRoute,
                onCancelButtonPressed = onCancelButtonPressed
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(dimensionResource(id = R.dimen.fab_small_size)),
                onClick = { setAddNewWordCardDialogVisibility(true) }) {
                Icon(Icons.Filled.Add, stringResource(R.string.sub_fab_content_desc))
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(words) { word ->
                Row {
                    Checkbox(
                        checked = word.checked,
                        onCheckedChange = { checked -> onWordCheckedStateChange(word, checked) })
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = word.quantity.toString(),
                        style = MaterialTheme.typography.caption
                    )
                    Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_short)))
                    TextField(
                        modifier = Modifier.weight(WEIGHT_STD),
                        textStyle = MaterialTheme.typography.subtitle2,
                        value = word.originalWord,
                        onValueChange = { newValue -> onOrigWordChange(newValue, word) })
                    Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_short)))
                    TextField(
                        modifier = Modifier
                            .weight(WEIGHT_STD),
                        textStyle = MaterialTheme.typography.subtitle2,
                        value = word.translatedWord,
                        trailingIcon = {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.edit_scr_edit_icon_desc),
                                modifier = Modifier.clickable { onTranslationClick(word) }
                            )
                        },
                        onValueChange = { newValue -> onTranslationChange(newValue, word) })
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
        OutlinedButton(
            modifier = Modifier.weight(WEIGHT_STD),
            onClick = {
                onOkButtonPressed()
                onOkButtonPressedRoute()
            }
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
    isFirstLoad: Boolean,
    onSubtitleNameChange: (String) -> Unit,
    origLanguage: Language,
    languages: Array<Language>,
    onSubtitlesLanguageChange: (Language) -> Unit,
    targetLanguage: Language,
    onTargetLanguageChange: (Language) -> Unit,
    uncheckedToDict: Boolean,
    onChangeUncheckedToDict: (Boolean) -> Unit,
    onTranslateButtonClicked: () -> Unit,
    updateCommonsAndReloadFile: () -> Unit,
    saveListToCsvFile: (Uri) -> Unit
) {

    var origLanguageMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var targetLanguageMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val fullTopBar = rememberSaveable { (mutableStateOf(true)) }

    val exportSubsInCSVLauncher = rememberLauncherForActivityResult(
        CreateDocument(CSV_FILE_MIME_TYPE)
    ) { docUri: Uri? ->
        docUri?.let {
            saveListToCsvFile(docUri)
        }
    }

    Card(
        elevation = dimensionResource(id = R.dimen.elevation_std),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_short))
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { exportSubsInCSVLauncher.launch(subtitlesName ?: "") }) {
                    Icon(Icons.Outlined.Save,
                        stringResource(R.string.edit_scr_export_into_csv))
                }
                IconButton(onClick = { fullTopBar.value = !fullTopBar.value }) {
                    if (fullTopBar.value) {
                        Icon(
                            Icons.Outlined.Close,
                            stringResource(R.string.edit_scr_close_topbar_desc)
                        )
                    } else {
                        Icon(
                            Icons.Outlined.ArrowDropDown,
                            stringResource(R.string.edit_scr_open_topbar_desc)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = fullTopBar.value,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
                content = {
                    Column {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth(),
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
                                    onDismissRequest = { origLanguageMenuExpanded = false },
                                    offset = DpOffset(
                                        x = dimensionResource(id = R.dimen.std_x_offset),
                                        y = 0.dp
                                    )
                                ) {
                                    for (language in languages) {
                                        ClickableText(
                                            modifier = Modifier
                                                .padding(dimensionResource(id = R.dimen.padding_std)),
                                            text = AnnotatedString(
                                                language.name
                                                        + " " + stringResource(id = language.fullNameResource),
                                            ),
                                            style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.onBackground),
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
                                            text = AnnotatedString(
                                                language.name
                                                        + " " + stringResource(id = language.fullNameResource)
                                            ),
                                            style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.onBackground),
                                            onClick = {
                                                targetLanguageMenuExpanded = false
                                                onTargetLanguageChange(language)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onTranslateButtonClicked
                        ) {
                            Text(stringResource(R.string.button_translate))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = uncheckedToDict,
                                onCheckedChange = { onChangeUncheckedToDict(it) })
                            Text(
                                modifier = Modifier.weight(WEIGHT_STD),
                                text = stringResource(R.string.add_unchecked_text),
                                style = MaterialTheme.typography.caption
                            )
                            if (isFirstLoad) {
                                OutlinedButton(
                                    modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.padding_std_doubled))
                                        .weight(HALF_WEIGHT),
                                    onClick = updateCommonsAndReloadFile
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = stringResource(R.string.reload_file_desc),
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
