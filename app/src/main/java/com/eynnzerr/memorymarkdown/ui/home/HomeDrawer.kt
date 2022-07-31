package com.eynnzerr.memorymarkdown.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eynnzerr.memorymarkdown.R

@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val iconTextColor = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.6f)
    val backgroundColor = if (isSelected) colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent

    Surface(
        modifier = modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector =  imageVector,
                    contentDescription = title,
                    tint = iconTextColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = iconTextColor
                )
            }
        }
    }
}