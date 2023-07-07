package com.github.kutyrev.vocabulator.features.mainlist

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.features.cards.model.EMPTY_LIST_ID
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordsCount
import com.github.kutyrev.vocabulator.utils.getFileName
import kotlinx.coroutines.launch

private const val DEF_WEIGHT = 1.0f
private const val HALF_WEIGHT = 0.5f

@Composable
fun MainStructureScreen(
    listState: State<List<SubtitlesUnit>>,
    wordsCountState: State<List<WordsCount>>,
    unswipedSubtitleUnit: SubtitlesUnit?,
    onListItemClick: (Int) -> Unit,
    onEditButtonClick: (Int) -> Unit,
    onSettingsMenuItemClick: () -> Unit,
    onCommonsButtonClick: () -> Unit,
    onAboutButtonClick: () -> Unit,
    onTutorialButtonClick: () -> Unit,
    checkFileExtension: (String) -> Boolean,
    setLanguage: (Language?) -> Unit,
    loadFile: (Uri, String?) -> Unit,
    onSubtitleSwiped: (subtitleUnit: SubtitlesUnit) -> Unit,
    setUnswipedSubtitleUnit: (subtitleUnit: SubtitlesUnit?) -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val pickSubtitlesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { docUri ->
        if (docUri != null) {
            val fileName: String = getFileName(context, docUri) ?: ""
            if (checkFileExtension(fileName)) {
                loadFile(docUri, fileName)
            } else {
                setLanguage(null)
                Toast.makeText(
                    context, context.getText(R.string.toast_unsupported_file), Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val onDeleteShowSnackbar: (SubtitlesUnit) -> Unit = { subtitlesUnit ->
        coroutineScope.launch {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = context.getString(
                    R.string.snackbar_delete_sub_unit_text,
                    subtitlesUnit.name
                ),
                actionLabel = context.getString(R.string.snackbar_undo_button)
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> onSubtitleSwiped(subtitlesUnit)
                SnackbarResult.ActionPerformed -> setUnswipedSubtitleUnit(subtitlesUnit)
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState, topBar = {
        MainListTopAppBar(
            onCommonsButtonClick,
            onSettingsMenuItemClick,
            onAboutButtonClick,
            onTutorialButtonClick
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = { showLanguageDialog = true }) {
            Icon(Icons.Filled.Add, stringResource(R.string.sub_fab_content_desc))
        }
    }) { paddingValues ->
        MainListScreen(
            listState = listState,
            wordsCountState = wordsCountState,
            unswipeSubtitleUnit = unswipedSubtitleUnit,
            onListItemClick = onListItemClick,
            onEditButtonClick = onEditButtonClick,
            onSubtitleSwiping = onDeleteShowSnackbar,
            setUnswipedSubtitleUnit = setUnswipedSubtitleUnit,
            paddingValues = paddingValues
        )
    }

    if (showLanguageDialog) {
        Dialog(onDismissRequest = {
            showLanguageDialog = false
            // pickSubtitlesLauncher.launch(arrayOf("*/*"))
        }) {
            Surface(
                elevation = dimensionResource(id = R.dimen.elevation_std),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_std))
            ) {
                Column {
                    Text(
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_std)),
                        text = stringResource(R.string.choose_lang_dialog_caption),
                        style = MaterialTheme.typography.caption
                    )
                    for (language in Language.values()) {
                        Row {
                            ClickableText(
                                modifier = Modifier.padding(dimensionResource(R.dimen.padding_std)),
                                style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.onBackground),
                                onClick = {
                                    showLanguageDialog = false
                                    setLanguage(language)
                                    pickSubtitlesLauncher.launch(arrayOf("*/*"))
                                },
                                text = AnnotatedString(language.name).plus(
                                    AnnotatedString(
                                        stringResource(language.fullNameResource)
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainListTopAppBar(
    onCommonsButtonClick: () -> Unit,
    onSettingsMenuItemClick: () -> Unit,
    onAboutButtonClick: () -> Unit,
    onTutorialButtonClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(elevation = dimensionResource(id = R.dimen.elevation_std),
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = onCommonsButtonClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_fact_check_24),
                    contentDescription = stringResource(id = R.string.button_commons)
                )
            }

            IconButton(onClick = {
                showMenu = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.topappbar_options_button_desc)
                )
            }

            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(onClick = onTutorialButtonClick) {
                    Text(stringResource(R.string.menuitem_tutorial))
                }
                DropdownMenuItem(onClick = onSettingsMenuItemClick) {
                    Text(stringResource(R.string.menuitem_settings))
                }
                DropdownMenuItem(onClick = onAboutButtonClick) {
                    Text(stringResource(R.string.menuitem_about))
                }
            }
        })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainListScreen(
    listState: State<List<SubtitlesUnit>>,
    wordsCountState: State<List<WordsCount>>,
    unswipeSubtitleUnit: SubtitlesUnit?,
    onListItemClick: (Int) -> Unit,
    onEditButtonClick: (Int) -> Unit,
    onSubtitleSwiping: (subtitleUnit: SubtitlesUnit) -> Unit,
    setUnswipedSubtitleUnit: (subtitleUnit: SubtitlesUnit?) -> Unit,
    paddingValues: PaddingValues
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        LazyColumn(modifier = Modifier) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_std))
                        .clickable { onListItemClick(EMPTY_LIST_ID) },
                ) {
                    Text(
                        text = stringResource(R.string.main_scr_all_cards_item),
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(listState.value) { subtitlesUnit ->
                val dismissState = rememberDismissState(confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) onSubtitleSwiping(subtitlesUnit)
                    true
                })

                val cardsCount =
                    wordsCountState.value.find { it.subId == subtitlesUnit.id }?.wordsCount ?: 0

                if (unswipeSubtitleUnit == subtitlesUnit) {
                    LaunchedEffect(Unit) {
                        dismissState.reset()
                        setUnswipedSubtitleUnit(null)
                    }
                }

                val color by animateColorAsState(
                    when (dismissState.targetValue) {
                        DismissValue.Default -> Color.White
                        DismissValue.DismissedToStart -> Color.Red
                        else -> Color.LightGray
                    }
                )

                if (!dismissState.isDismissed(DismissDirection.EndToStart)) {

                    SwipeToDismiss(state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(
                                        horizontal = dimensionResource(
                                            id = R.dimen.padding_std
                                        ),
                                        vertical = dimensionResource(
                                            id = R.dimen.padding_std
                                        )
                                    ),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.main_list_delete_subs),
                                )
                            }
                        },
                        dismissContent = {
                            Card(
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_std))
                                    .fillMaxWidth()
                                    .clickable(onClick = { onListItemClick(subtitlesUnit.id) }),
                                elevation = dimensionResource(id = R.dimen.elevation_std),
                                shape = CutCornerShape(topStart = dimensionResource(id = R.dimen.corner_radius_std))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier
                                            .padding(dimensionResource(id = R.dimen.padding_std))
                                            .weight(DEF_WEIGHT),
                                        text = subtitlesUnit.name +
                                                if (cardsCount > 0) stringResource(
                                                    id = R.string.cards_count,
                                                    cardsCount
                                                ) else "",
                                        style = MaterialTheme.typography.body2
                                    )
                                    OutlinedButton(modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.padding_std))
                                        .weight(HALF_WEIGHT),
                                        onClick = { onEditButtonClick(subtitlesUnit.id) }) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                                            contentDescription = stringResource(
                                                id = R.string.edit_button_desc
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
