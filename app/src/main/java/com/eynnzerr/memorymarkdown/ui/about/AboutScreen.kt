package com.eynnzerr.memorymarkdown.ui.about

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun AboutScreen(
    navigateBack: () -> Unit
) {
    // About me: github page, email
    // Contributes to:
    // Version
    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                     ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Text(
            text = "to be implemented.",
            modifier = Modifier.padding(it)
        )
    }
}