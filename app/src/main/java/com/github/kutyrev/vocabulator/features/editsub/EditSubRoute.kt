package com.github.kutyrev.vocabulator.features.editsub

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kutyrev.vocabulator.features.editsub.model.EditSubViewModel

@Composable
fun EditSubRoute(viewModel: EditSubViewModel = hiltViewModel()) {
    Surface() {
        EditSubScreen()
    }
}
