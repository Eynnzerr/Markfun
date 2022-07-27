package com.eynnzerr.memorymarkdown.ui.setting

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import com.eynnzerr.memorymarkdown.ui.theme.IconButtonColor
import com.eynnzerr.memorymarkdown.ui.theme.IconColor

private const val TAG = "SettingScreen"

@ExperimentalMaterial3Api
@Composable
fun SettingScreen(
    navController: NavHostController
) {

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
                }
                SettingItem(
                    resourceId = R.drawable.setting_theme,
                    title = stringResource(id = R.string.setting_theme)
                ) {
                    // TODO Choose Markdown preview theme from list
                }
            }
            SettingItem(
                imageVector = Icons.Filled.Dashboard,
                title = stringResource(id = R.string.setting_advanced)) {

            }
            SettingItem(
                resourceId = R.drawable.setting_about,
                title = stringResource(id = R.string.setting_about)) {

            }
        }
    }
}

@Composable
private fun SettingGroup(
    imageVector: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    var expand by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingItem(imageVector = imageVector, title = title) {
            expand = ! expand
        }
        Icon(
            imageVector = if (expand) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowLeft,
            contentDescription = null
        )
    }

    AnimatedVisibility(visible = expand) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

@Composable
private fun SettingGroup(
    resourceId: Int,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    var expand by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp)
            .clickable { expand = !expand },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Icon(
                painter = painterResource(id = resourceId),
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            Text(text = title)
        }
        Icon(
            imageVector = if (expand) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowLeft,
            contentDescription = null,
            tint = IconButtonColor,
            modifier = Modifier.size(32.dp)
        )
    }

    AnimatedVisibility(
        visible = expand,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

@Composable
private fun SettingItem(imageVector: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = title,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.padding(horizontal = 16.dp))
        Text(text = title)
    }
}

@Composable
private fun SettingItem(resourceId: Int, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = resourceId),
            contentDescription = title,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.padding(horizontal = 16.dp))
        Text(text = title)
    }
}