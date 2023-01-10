package com.github.kutyrev.vocabulator.features.editsub

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.github.kutyrev.vocabulator.R

@Composable
fun EditSubScreen() {
    var text by remember { mutableStateOf("Hello") }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(id = R.dimen.padding_std)),
        value = text,
        onValueChange = { text = it },
        label = { Text(stringResource(id = R.string.subtitles_name_label)) }
    )

    LazyColumn(modifier = Modifier) {

    }

}
