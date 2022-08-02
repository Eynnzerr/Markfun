package com.eynnzerr.memorymarkdown.ui.home

import android.util.Log
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.eynnzerr.memorymarkdown.data.database.MarkdownData
import io.noties.markwon.Markwon

@Composable
fun HomeListItem(
    modifier: Modifier = Modifier,
    data: MarkdownData,
    markwon: Markwon,
    onStarred: (MarkdownData) -> Unit,
    onClick: (MarkdownData) -> Unit,
    onLongPressed: (MarkdownData) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            //.clickable { onClick(data) },
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick(data) },
                    onLongPress = { onLongPressed(data) }
                )
            },
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary
            ) {

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 6.dp, bottom = 16.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    modifier = Modifier.clickable {
                        onStarred(data) },
                    imageVector = if (data.isStarred == MarkdownData.IS_STARRED) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        maxLines = 6
                        markwon.setMarkdown(this, data.content)
                    }
                },
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun HomeList(
    modifier: Modifier = Modifier,
    dataList: List<MarkdownData>,
    markwon: Markwon,
    listState: LazyListState = rememberLazyListState(),
    onStarredItem: (MarkdownData) -> Unit,
    onClickItem: (MarkdownData) -> Unit,
    onLongPressItem: (MarkdownData) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(dataList, key = { it.id }) { data ->
            HomeListItem(
                modifier = Modifier.animateItemPlacement(),
                data = data,
                markwon = markwon,
                onStarred = onStarredItem,
                onClick = onClickItem,
                onLongPressed = onLongPressItem
            )
        }
    }
}

private const val TAG = "HomeList"