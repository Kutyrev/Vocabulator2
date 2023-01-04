package com.github.kutyrev.vocabulator.features.mainlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.R
import com.github.kutyrev.vocabulator.features.mainlist.model.MainListViewModel
import com.github.kutyrev.vocabulator.model.SubtitlesUnit

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainListRoute(viewModel: MainListViewModel = hiltViewModel(), onListItemClick: (Int) -> Unit) {
    val listState = viewModel.subtitlesList.collectAsStateWithLifecycle(initialValue = listOf())
    MainListScreen(listState, onListItemClick)
}

@Composable
private fun MainListScreen(listState: State<List<SubtitlesUnit>>, onListItemClick: (Int) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier) {
            items(listState.value) { subtitlesUnit ->
                Card(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_std))
                        .fillMaxWidth()
                        .clickable(onClick = { onListItemClick(subtitlesUnit.id) }),
                    elevation = 4.dp
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = subtitlesUnit.name,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

