package com.eynnzerr.memorymarkdown.ui.setting

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.eynnzerr.memorymarkdown.base.CPApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "SettingScreen"

@ExperimentalMaterial3Api
@Composable
fun SettingScreen() {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        Log.d(TAG, "SettingScreen: uri is $uri")
        uri?.let {
            scope.launch(Dispatchers.IO) {
                context.contentResolver.openOutputStream(it)?.writer()?.run { 
                    write("testhahaha")
                    flush()
                    close()
                }
            }
            Log.d(TAG, "SettingScreen: over.")
        }
        
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {}
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Button(onClick = { launcher.launch("test.md") }) {
                Text(text = "test")
            }
        }
    }
}