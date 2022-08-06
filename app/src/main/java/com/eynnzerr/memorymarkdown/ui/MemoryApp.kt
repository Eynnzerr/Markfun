package com.eynnzerr.memorymarkdown.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.navigation.NavGraph
import com.eynnzerr.memorymarkdown.ui.theme.MemoryMarkdownTheme

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun MemoryApp() {
    val themeState by MMKVUtils.themeState.collectAsState()
    val navHostController = rememberNavController()

//    val darkTheme = isSystemInDarkTheme()
//    val markdownTextColor = if (darkTheme) Color.White.toArgb() else Color.Black.toArgb()
//    val LocalMarkdownTextColor = compositionLocalOf { markdownTextColor }

    MemoryMarkdownTheme(
        //darkTheme = darkTheme,
        baseColor = themeState.appTheme
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
//                CompositionLocalProvider(LocalMarkdownTextColor provides markdownTextColor) {
//                    NavGraph(navHostController)
//                }
                NavGraph(navHostController)
            }
        }
    }
}
