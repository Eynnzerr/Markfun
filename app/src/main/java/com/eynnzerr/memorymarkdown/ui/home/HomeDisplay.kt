package com.eynnzerr.memorymarkdown.ui.home

import android.text.TextUtils
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.data.ListDisplayMode
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
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick(data) },
                    onLongPress = { onLongPressed(data) }
                )
            },
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.small,
        border = if (isSystemInDarkTheme()) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary
            ) { }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 6.dp, bottom = 6.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier.clickable {
                        onStarred(data) },
                    imageVector = if (data.isStarred == MarkdownData.IS_STARRED) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(id = R.string.time_created) + data.createdDate,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(id = R.string.time_modified) + data.modifiedDate,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        maxLines = 6
                        setTextColor(textColor)
                        ellipsize = TextUtils.TruncateAt.MIDDLE
                        markwon.setMarkdown(this, data.content)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    // .clickable { onClick(data) }
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

@ExperimentalFoundationApi
@Composable
fun HomeGrid(
    modifier: Modifier = Modifier,
    dataList: List<MarkdownData>,
    markwon: Markwon,
    gridState: LazyGridState = rememberLazyGridState(),
    onStarredItem: (MarkdownData) -> Unit,
    onClickItem: (MarkdownData) -> Unit,
    onLongPressItem: (MarkdownData) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 128.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = gridState
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

@ExperimentalFoundationApi
@Composable
fun HomeDisplay(
    modifier: Modifier = Modifier,
    displayMode: Int,
    dataList: List<MarkdownData>,
    markwon: Markwon,
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState(),
    onStarredItem: (MarkdownData) -> Unit,
    onClickItem: (MarkdownData) -> Unit,
    onLongPressItem: (MarkdownData) -> Unit
) {
    if (displayMode == ListDisplayMode.IN_LIST) {
        HomeList(
            modifier = modifier,
            dataList = dataList,
            markwon = markwon,
            listState = listState,
            onStarredItem = onStarredItem,
            onClickItem = onClickItem,
            onLongPressItem = onLongPressItem
        )
    }
    else {
        HomeGrid(
            modifier = modifier,
            dataList = dataList,
            markwon = markwon,
            gridState = gridState,
            onStarredItem = onStarredItem,
            onClickItem = onClickItem,
            onLongPressItem = onLongPressItem
        )
    }
}

private const val TAG = "HomeList"