package com.eynnzerr.memorymarkdown.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.eynnzerr.memorymarkdown.ui.theme.MemoryMarkdownTheme
import com.eynnzerr.memorymarkdown.utils.UriUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        setImmerseStatusBar(window)

        if (intent.action == Intent.ACTION_VIEW) {
            val uri = Uri.parse(intent.dataString)
            UriUtils.prepareUri(uri)
        }

        setContent {
            MemoryApp()
        }
    }
}

private fun setImmerseStatusBar(window: Window) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
        v.setPadding(0, 0, 0, 0)
        insets
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MemoryMarkdownTheme {
        MemoryApp()
    }
}

private const val TAG = "MainActivity"