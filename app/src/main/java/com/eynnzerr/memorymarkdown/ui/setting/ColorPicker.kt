package com.eynnzerr.memorymarkdown.ui.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ColorPicker(colors: List<AppColor>, initialIndex: Int, onClickItem: (Int, Int) -> Unit) {
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        border = BorderStroke(2.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectColor(color.colorArgb) }
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(color.colorArgb)
            ) {}

            Text(
                text = color.name,
                fontSize = 24.sp
            )
        }
    }
}