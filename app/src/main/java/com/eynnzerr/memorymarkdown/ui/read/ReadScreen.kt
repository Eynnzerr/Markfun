package com.eynnzerr.memorymarkdown.ui.read

import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import com.eynnzerr.memorymarkdown.ui.theme.IconButtonColor

@ExperimentalMaterial3Api
@Composable
fun ReadScreen(
    navController: NavHostController,
    viewModel: ReadViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateTo(Destinations.HOME_ROUTE)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = IconButtonColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // TODO 携带当前页面内容跳转到编辑页面。如果当前是Uri，则传递Uri；如果当前是Room数据，则传递文件名即可，之后更新Room表项及重写本地文件
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = IconButtonColor
                        )
                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 13.dp),
                maxLines = 1
            )
            AndroidView(
                factory = { context ->
                    TextView(context).also { textView ->
                        Log.d(TAG, "ReadScreen: content is: ${uiState.content}")
                        viewModel.getMarkwon().setMarkdown(textView, uiState.content)
                    }
                },
                modifier = Modifier.padding(13.dp),
                update = { textView ->
                    viewModel.getMarkwon().setMarkdown(textView, uiState.content)
                }
            )
        }
    }
}

private const val TAG = "ReadScreen"