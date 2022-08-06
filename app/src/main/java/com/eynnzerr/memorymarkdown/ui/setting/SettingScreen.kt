package com.eynnzerr.memorymarkdown.ui.setting

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import com.eynnzerr.memorymarkdown.navigation.navigateToSingle

private const val TAG = "SettingScreen"

@ExperimentalMaterial3Api
@Composable
fun SettingScreen(
    navController: NavHostController,
    viewModel: SettingViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // dialogs
    var openColorPicker by remember { mutableStateOf(false) }
    var openHelpDialog by remember { mutableStateOf(false) }

    if (openColorPicker) {
        AlertDialog(
            onDismissRequest = { openColorPicker = false },
            title = { Text(text = stringResource(id = R.string.setting_theme_hint)) },
            confirmButton = {
                Button(
                    onClick = {
                        openColorPicker = false
                    },
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(id = R.string.setting_confirm),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.setting_confirm),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            text = {
                ColorPicker(colors = defaultColors, initialIndex = uiState.initColorIndex) { color, index ->
                    viewModel.updateAppTheme(color, index)
                }
            }
        )
    }
    
    if (openHelpDialog) {
        AlertDialog(
            onDismissRequest = { openHelpDialog = false },
            title = {},
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        openHelpDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.HelpOutline,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            /*
            设置选项：
            主题 select done.
            配色 select TODO
            是否自动导出 switch done.
            高级用户偏好设置 navigate TODO
            关于 navigate TODO
             */
            Spacer(modifier = Modifier.padding(vertical = 24.dp))
            SettingGroup(
                resourceId = R.drawable.setting_appearance,
                title = stringResource(id = R.string.setting_appearance)
            ) {
                SettingItem(
                    resourceId = R.drawable.setting_palette,
                    title = stringResource(id = R.string.setting_color)
                ) {
                    // Choose app color from color picker
                    openColorPicker = true
                }
                SettingItem(
                    resourceId = R.drawable.setting_theme,
                    title = stringResource(id = R.string.setting_theme)
                ) {
                    // Choose Markdown preview theme from list
                    Toast.makeText(context, "This feature will be ready very soon.", Toast.LENGTH_SHORT).show()
                }
            }
            SettingGroup(
                imageVector = Icons.Filled.Dashboard,
                title = stringResource(id = R.string.setting_advanced)
            ) {
                SettingSwitch(
                    imageVector = Icons.Filled.Download, 
                    title = stringResource(id = R.string.setting_save),
                    checked = uiState.isAutomaticallySaveEnabled
                ) { enabled ->
                    viewModel.updateBackupPreference(enabled)
                }
            }
            SettingItem(
                resourceId = R.drawable.setting_about,
                title = stringResource(id = R.string.setting_about)) {
                navController.navigateToSingle(gitDestinations.ABOUT_ROUTE)
            }
        }
    }
}