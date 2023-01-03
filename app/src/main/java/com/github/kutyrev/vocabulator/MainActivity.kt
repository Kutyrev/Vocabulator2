package com.github.kutyrev.vocabulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.kutyrev.vocabulator.app.VocabulatorNavHost
import com.github.kutyrev.vocabulator.ui.theme.VocabulatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VocabulatorTheme {
                VocabulatorApp()
            }
        }
    }
}

@Composable
fun VocabulatorApp() {
    val navController = rememberNavController()

    Scaffold {
        innerPadding ->
        VocabulatorNavHost(navController = navController,
        modifier = Modifier.padding(innerPadding))
    }
}

