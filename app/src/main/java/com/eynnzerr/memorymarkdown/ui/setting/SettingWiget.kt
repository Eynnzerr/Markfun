package com.eynnzerr.memorymarkdown.ui.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingGroup(
    imageVector: ImageVector,
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
                imageVector = imageVector,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            Text(text = title)
        }
        Icon(
            imageVector = if (expand) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowLeft,
            contentDescription = null,
            // tint = IconButtonColor,
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
fun SettingGroup(
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
            // tint = IconButtonColor,
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
fun SettingItem(imageVector: ImageVector, title: String, onClick: () -> Unit) {
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
fun SettingItem(resourceId: Int, title: String, onClick: () -> Unit) {
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

@Composable
fun SettingSwitch(imageVector: ImageVector, title: String, checked: Boolean, onSwitch: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Icon(
                imageVector = imageVector,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            Text(text = title)
        }
        Switch(
            checked = checked,
            onCheckedChange = onSwitch
        )
    }
}