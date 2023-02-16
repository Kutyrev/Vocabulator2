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
import androidx.compose.ui.window.Dialog
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.utils.getFileName

private const val DEF_WEIGHT = 1.0f

@Composable
fun MainStructureScreen(
    listState: State<List<SubtitlesUnit>>,
    onListItemClick: (Int) -> Unit,
    onEditButtonClick: (Int) -> Unit,
    onSettingsMenuItemClick: () -> Unit,
    onCommonsButtonClick: () -> Unit,
    checkFileExtension: (String) -> Boolean,
    setLanguage: (Language?) -> Unit,
    loadFile: (Uri, String?) -> Unit,
    onSubtitleSwiped: (subtitleUnit: SubtitlesUnit) -> Unit
) {
    val showMenu by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val pickSubtitlesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { docUri ->
        if (docUri != null) {
            if (checkFileExtension(docUri.toString())) {
                loadFile(docUri, getFileName(context, docUri))
            } else {
                setLanguage(null)
                Toast.makeText(
                    context, context.getText(R.string.toast_unsupported_file), Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Scaffold(topBar = {
        MainListTopAppBar(onCommonsButtonClick, showMenu, onSettingsMenuItemClick)
    }, floatingActionButton = {
        FloatingActionButton(onClick = { showLanguageDialog = true }) {
            Icon(Icons.Filled.Add, stringResource(R.string.sub_fab_content_desc))
        }
    }) { paddingValues ->
        MainListScreen(
            listState = listState,
            onListItemClick = onListItemClick,
            onEditButtonClick = onEditButtonClick,
            onSubtitleSwiped = onSubtitleSwiped,
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
    onCommonsButtonClick: () -> Unit, showMenu: Boolean, onSettingsMenuItemClick: () -> Unit
) {
    var showMenu1 = showMenu
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
                showMenu1 = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.topappbar_options_button_desc)
                )
            }

            DropdownMenu(expanded = showMenu1, onDismissRequest = { showMenu1 = false }) {
                DropdownMenuItem(onClick = { /*TODO*/ }) {
                    Text(stringResource(R.string.menuitem_tutorial))
                }
                DropdownMenuItem(onClick = { onSettingsMenuItemClick() }) {
                    Text(stringResource(R.string.menuitem_settings))
                }
                DropdownMenuItem(onClick = { /*TODO*/ }) {
                    Text(stringResource(R.string.menuitem_about))
                }
            }
        })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainListScreen(
    listState: State<List<SubtitlesUnit>>,
    onListItemClick: (Int) -> Unit,
    onEditButtonClick: (Int) -> Unit,
    onSubtitleSwiped: (subtitleUnit: SubtitlesUnit) -> Unit,
    paddingValues: PaddingValues
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        LazyColumn(modifier = Modifier) {
            items(listState.value) { subtitlesUnit ->

                val dismissState = rememberDismissState(confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) onSubtitleSwiped(subtitlesUnit)
                    true
                })

                val color by animateColorAsState(
                    when (dismissState.targetValue) {
                        DismissValue.Default -> Color.White
                        DismissValue.DismissedToStart -> Color.Red
                        else -> Color.LightGray
                    }
                )

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
                            elevation = dimensionResource(id = R.dimen.elevation_std)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.padding_std))
                                        .weight(DEF_WEIGHT),
                                    text = subtitlesUnit.name,
                                    style = MaterialTheme.typography.body2
                                )
                                Button(modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_std))
                                    .weight(DEF_WEIGHT),
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
