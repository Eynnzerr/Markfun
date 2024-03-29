package com.eynnzerr.memorymarkdown.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eynnzerr.memorymarkdown.ui.theme.MemoryMarkdownTheme
import com.eynnzerr.memorymarkdown.utils.UriUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint

// For app performance's concern, though it's thread-unsafe,
// we do not constraint it as synchronized.
var mainActivityReady = false

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // To make navController accessible in onNewIntent()
    private lateinit var navHostController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        setImmerseStatusBar(window)
        handleIntent(intent)

        setContent {
            navHostController = rememberNavController()
            MemoryApp(navHostController)
        }

        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if home data is ready.
                    return if (mainActivityReady) {
                        // The content is ready; start drawing.
                        Log.d(TAG, "onPreDraw: ready to draw homeScreen.")
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        Log.d(TAG, "onPreDraw: not ready yet.")
                        false
                    }
                }
            }
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            setIntent(it)
            handleIntent(it)
        }
        navHostController.handleDeepLink(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            Log.d(TAG, "Receive deeplink uri: ${intent.dataString}")
            val uri = Uri.parse(intent.dataString)
            UriUtils.prepareUri(uri)
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

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MemoryMarkdownTheme {
        MemoryApp(rememberNavController())
    }
}

private const val TAG = "MainActivity"