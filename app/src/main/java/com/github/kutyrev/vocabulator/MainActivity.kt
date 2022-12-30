package com.github.kutyrev.vocabulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.kutyrev.vocabulator.features.mainlist.MainListRoute
import com.github.kutyrev.vocabulator.ui.theme.VocabulatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VocabulatorTheme {
                MainListRoute()
            }
        }
    }
}
