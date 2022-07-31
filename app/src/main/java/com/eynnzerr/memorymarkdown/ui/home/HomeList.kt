package com.eynnzerr.memorymarkdown.ui.home

import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.eynnzerr.memorymarkdown.data.database.MarkdownData
import io.noties.markwon.Markwon

@Composable
fun HomeListItem(data: MarkdownData, markwon: Markwon) {
    var expand by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expand = !expand },
        shadowElevation = 8.dp
    ) {
        Text(
            text = data.title,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        AnimatedVisibility(visible = expand) {
            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        maxLines = 8
                        markwon.setMarkdown(this, data.content)
                    }
                }
            )
        }
    }
}

@Composable
fun HomeList(
    modifier: Modifier = Modifier,
    dataList: List<MarkdownData>,
    markwon: Markwon
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(dataList) { data ->
            HomeListItem(data = data, markwon = markwon)
        }
    }
}