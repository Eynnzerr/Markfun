package com.eynnzerr.memorymarkdown.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.eynnzerr.memorymarkdown.navigation.NavGraph
import com.eynnzerr.memorymarkdown.ui.theme.MemoryMarkdownTheme

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun MemoryApp() {
    val navHostController = rememberNavController()
    MemoryMarkdownTheme {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavGraph(navHostController)
            }
        }
    }
}
