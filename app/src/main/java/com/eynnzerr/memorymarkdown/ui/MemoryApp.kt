package com.eynnzerr.memorymarkdown.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.navigation.NavGraph
import com.eynnzerr.memorymarkdown.ui.theme.MemoryMarkdownTheme

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun MemoryApp() {

    val themeState by MMKVUtils.themeState.collectAsState()
    val navHostController = rememberNavController()

    MemoryMarkdownTheme(
        baseColor = themeState.appTheme
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
                NavGraph(navHostController)
            }
        }
    }
}
