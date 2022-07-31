package com.eynnzerr.memorymarkdown.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.utils.UriUtils
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "Range")
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    //
    val uiState by viewModel.uiState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectionExpanded by remember { mutableStateOf(false) }

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
            navController.navigateTo(Destinations.WRITE_ROUTE)
        }
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
                        }
                        DrawerItem(
                            modifier = Modifier.padding(bottom = 8.dp),
                            imageVector = Icons.Filled.Visibility,
                            title = stringResource(id = R.string.drawer_viewed),
                            isSelected = uiState.homeType == HomeType.VIEWED) {
                            viewModel.switchType(HomeType.VIEWED)
                        }
                        DrawerItem(
                            modifier = Modifier.padding(bottom = 8.dp),
                            imageVector = Icons.Filled.Star,
                            title = stringResource(id = R.string.drawer_starred),
                            isSelected = uiState.homeType == HomeType.STARRED) {
                            viewModel.switchType(HomeType.STARRED)
                        }
                        DrawerItem(
                            modifier = Modifier.padding(bottom = 8.dp),
                            imageVector = Icons.Filled.Delete,
                            title = stringResource(id = R.string.drawer_archived),
                            isSelected = uiState.homeType == HomeType.ARCHIVED) {
                            viewModel.switchType(HomeType.ARCHIVED)
                        }
                    }

                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {},
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
                                onClick = { navController.navigateTo(Destinations.WRITE_ROUTE) },
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
        ) {
            // Display logo when data is empty
            if (uiState.homeList.isEmpty()) {
                Logo()
            }
            else {
                HomeList(
                    modifier = Modifier.padding(it),
                    dataList = uiState.homeList,
                    markwon = viewModel.markwon
                )
            }
        }
    }
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
            textAlign = TextAlign.Center
        )
    }
}

private const val TAG = "HomeScreen"
