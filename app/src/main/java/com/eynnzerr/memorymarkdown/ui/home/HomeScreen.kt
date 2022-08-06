package com.eynnzerr.memorymarkdown.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.data.database.MarkdownData
import com.eynnzerr.memorymarkdown.utils.UriUtils
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@ExperimentalFoundationApi
@SuppressLint("Range")
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectionExpanded by remember { mutableStateOf(false) }
    var openDeleteDialog by remember { mutableStateOf(false) }

    var animalBoolean by remember { mutableStateOf(true) }
    val animalBooleanState: Float by animateFloatAsState(
        if (animalBoolean) {
            0f
        } else {
            1f
        }, animationSpec = TweenSpec(durationMillis = 600)
    )
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        it?.let {
            UriUtils.run {
                uri = it
                isUriValid = true
            }
            navController.navigateTo(Destinations.WRITE_ROUTE + "/-1")
        }
    }

    if (openDeleteDialog) {
        val hint = if (viewModel.tempData.status == MarkdownData.STATUS_ARCHIVED)
                        stringResource(id = R.string.delete_permanently)
                    else stringResource(id = R.string.delete_hint)
        AlertDialog(
            onDismissRequest = { openDeleteDialog = false },
            text = {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.tempData.status == MarkdownData.STATUS_ARCHIVED) {
                            viewModel.deleteMarkdown(viewModel.tempData)
                        }
                        else {
                            viewModel.updateMarkdown(
                                viewModel.tempData.copy(
                                    status = MarkdownData.STATUS_ARCHIVED,
                                    isStarred = MarkdownData.NOT_STARRED
                                )
                            )
                        }
                        openDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.write_confirm),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.write_cancel),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }

    ModalNavigationDrawer(
        scrimColor = Color.Transparent.copy(alpha = 0.5f),
        drawerContainerColor = Color.Transparent,
        drawerShape = RectangleShape,
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            Column(
                modifier = Modifier
                    .requiredWidth(300.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Spacer(modifier = Modifier.padding(top = 80.dp))
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = stringResource(id = R.string.app_name),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            fontSize = 20.sp
                        )
                        // 本地创建 收藏 最近打开 最近删除
                        DrawerItem(
                            imageVector = Icons.Filled.Source,
                            title = stringResource(id = R.string.drawer_created),
                            isSelected = uiState.homeType == HomeType.CREATED) {
                            viewModel.switchType(HomeType.CREATED)
                            scope.launch {
                                drawerState.close()
                            }
                        }
                        DrawerItem(
                            modifier = Modifier.padding(bottom = 8.dp),
                            imageVector = Icons.Filled.Visibility,
                            title = stringResource(id = R.string.drawer_viewed),
                            isSelected = uiState.homeType == HomeType.VIEWED) {
                            viewModel.switchType(HomeType.VIEWED)
                            scope.launch {
                                drawerState.close()
                            }
                        }
                        DrawerItem(
                            modifier = Modifier.padding(bottom = 8.dp),
                            imageVector = Icons.Filled.Star,
                            title = stringResource(id = R.string.drawer_starred),
                            isSelected = uiState.homeType == HomeType.STARRED) {
                            viewModel.switchType(HomeType.STARRED)
                            scope.launch {
                                drawerState.close()
                            }
                        }
                        DrawerItem(
                            modifier = Modifier.padding(bottom = 8.dp),
                            imageVector = Icons.Filled.Delete,
                            title = stringResource(id = R.string.drawer_archived),
                            isSelected = uiState.homeType == HomeType.ARCHIVED) {
                            viewModel.switchType(HomeType.ARCHIVED)
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    }

                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface (
                    shadowElevation = if (listState.isScrolled) 8.dp else 0.dp
                ) {
                    SmallTopAppBar(
                        modifier = Modifier.statusBarsPadding(),
                        title = { HomeBarTitle(type = uiState.homeType) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Filled.Sort,
                                    contentDescription = null
                                )
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null
                                )
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                Row (
                    modifier = Modifier
                        .padding(end = 20.dp, bottom = 20.dp)
                        .navigationBarsPadding()
                ) {
                    AnimatedVisibility(
                        visible = selectionExpanded,
                        enter = scaleIn(),
                        exit = scaleOut() // TODO 改为expand和shrink
                    ) {
                        Row {
                            FloatingActionButton(
                                onClick = { navController.navigateTo(Destinations.SETTING_ROUTE) },
                                shape = CircleShape,
                                modifier = Modifier.padding(end = 20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = null,
                                    // tint = IconColor
                                )
                            }
                            FloatingActionButton(
                                onClick = {
                                    launcher.launch(arrayOf("text/markdown"))
                                },
                                shape = CircleShape,
                                modifier = Modifier.padding(end = 20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FileOpen,
                                    contentDescription = null,
                                    // tint = IconColor
                                )
                            }
                            FloatingActionButton(
                                onClick = { navController.navigateTo(Destinations.WRITE_ROUTE + "/-1") },
                                shape = CircleShape,
                                modifier = Modifier.padding(end = 20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    // tint = IconColor
                                )
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = {
                            selectionExpanded = !selectionExpanded
                            animalBoolean = !animalBoolean
                        },
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HomeRepairService,
                            contentDescription = null,
                            // tint = IconColor,
                            modifier = Modifier.rotate(animalBooleanState * 360)
                        )
                    }
                }
            }
        ) { it ->
            // Display logo when data is empty
            if (uiState.homeList.isEmpty()) {
                Logo()
            }
            else {
                HomeList(
                    modifier = Modifier
                        .padding(it)
                        .navigationBarsPadding(),
                    dataList = uiState.homeList,
                    markwon = viewModel.markwon,
                    listState = listState,
                    onStarredItem = { data ->
                        viewModel.updateMarkdown(
                            data.copy(isStarred = (data.isStarred-1).absoluteValue)
                        )
                    },
                    onClickItem = { data ->
                        data.uri?.let { uri ->
                            UriUtils.prepareUri(uri)
                        }
                        navController.navigateTo(Destinations.WRITE_ROUTE + "/${data.id}")
                    },
                    onLongPressItem = {
                        viewModel.tempData = it
                        openDeleteDialog = true
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeBarTitle(type: HomeType) {
    val title = when (type) {
        HomeType.CREATED -> stringResource(id = R.string.title_created)
        HomeType.VIEWED -> stringResource(id = R.string.title_viewed)
        HomeType.STARRED -> stringResource(id = R.string.drawer_starred)
        HomeType.ARCHIVED -> stringResource(id = R.string.drawer_archived)
    }
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@Composable
private fun Logo() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.markdown_line),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer
        )
        Text(
            text = stringResource(id = R.string.home_hint),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private val LazyListState.isScrolled: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0

private const val TAG = "HomeScreen"
