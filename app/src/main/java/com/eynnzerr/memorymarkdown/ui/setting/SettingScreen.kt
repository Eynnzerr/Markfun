package com.eynnzerr.memorymarkdown.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo

private const val TAG = "SettingScreen"

@ExperimentalMaterial3Api
@Composable
fun SettingScreen(
    navController: NavHostController,
    viewModel: SettingViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

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
                        navController.navigateTo(Destinations.HOME_ROUTE)
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
            主题 select
            配色 select
            是否自动导出 switch
            高级用户偏好设置 navigate
            关于 navigate
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
                    // TODO Choose app color from color picker
                    //colorDialog.show()
                    openColorPicker = true
                }
                SettingItem(
                    resourceId = R.drawable.setting_theme,
                    title = stringResource(id = R.string.setting_theme)
                ) {
                    // TODO Choose Markdown preview theme from list
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

            }
        }
    }
}

@Composable
private fun ColorPicker(colors: List<AppColor>, initialIndex: Int, onClickItem: (Int, Int) -> Unit) {
    var selectedIndex by remember { mutableStateOf(initialIndex) }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        itemsIndexed(colors) { index, color ->
            ColorItem(color = color, selected = index == selectedIndex) {
                onClickItem(it, index)
                selectedIndex = index
            }
        }
    }
}

@Composable
private fun ColorItem(color: AppColor, selected: Boolean, onSelectColor: (Int) -> Unit) {
    // 输入： 颜色和名称
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectColor(color.colorArgb) }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color(color.colorArgb)
        ) {
            if (selected) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Filled.Done,
                    contentDescription = color.name
                )
            }
        }

        Text(
            text = color.name,
            fontSize = 20.sp
        )
    }
}