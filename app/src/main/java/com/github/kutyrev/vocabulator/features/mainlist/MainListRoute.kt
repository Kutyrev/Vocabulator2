package com.github.kutyrev.vocabulator.features.mainlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kutyrev.vocabulator.features.mainlist.model.MainListViewModel
import com.github.kutyrev.vocabulator.model.SubtitlesUnit

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainListRoute(viewModel: MainListViewModel = hiltViewModel()) {
    val listState = viewModel.subtitlesList.collectAsStateWithLifecycle(initialValue = listOf())
    MainListScreen(listState)
}

@Composable
private fun MainListScreen(listState: State<List<SubtitlesUnit>>) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn() {
            items(listState.value) { subtitlesUnit ->
                Card() {
                    Text(text = subtitlesUnit.name)
                }
            }
        }
    }
}
