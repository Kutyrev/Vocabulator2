package com.github.kutyrev.vocabulator.features.mainlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.features.mainlist.model.MainListViewModel
import com.github.kutyrev.vocabulator.model.SubtitlesUnit

private const val SPACER_DEF_WEIGHT = 1.0f

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainListRoute(
    viewModel: MainListViewModel = hiltViewModel(),
    onListItemClick: (Int) -> Unit,
    onEditButtonClick: (Int) -> Unit
) {
    val listState = viewModel.subtitlesList.collectAsStateWithLifecycle(initialValue = listOf())
    MainListScreen(listState, onListItemClick, onEditButtonClick)
}

@Composable
private fun MainListScreen(
    listState: State<List<SubtitlesUnit>>,
    onListItemClick: (Int) -> Unit,
    onEditButtonClick: (Int) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier) {
            items(listState.value) { subtitlesUnit ->
                Card(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_std))
                        .fillMaxWidth()
                        .clickable(onClick = { onListItemClick(subtitlesUnit.id) }),
                    elevation = dimensionResource(id = R.dimen.elevation_std)
                ) {
                    Row {
                        Text(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)),
                            text = subtitlesUnit.name,
                            style = MaterialTheme.typography.body1
                        )
                        Spacer(modifier = Modifier.weight(SPACER_DEF_WEIGHT))
                        Button(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)),
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
        }
    }
}

