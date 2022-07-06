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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import com.eynnzerr.memorymarkdown.ui.theme.IconButtonColor
import com.eynnzerr.memorymarkdown.ui.theme.IconColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
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
    ) { uri ->
        uri?.let {
            val file = it.toFile()
            Log.d(TAG, "HomeScreen: read markdown from ${file.path}")
            val title = file.name // 要去掉后缀
            with(file.reader()) {
                scope.launch(Dispatchers.IO) {
                    val content = readText()
                    viewModel.loadMarkdown(title, content)
                }
                close()
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
                    shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp),
                    color = Color.White
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontSize = 20.sp
                    )
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
                                contentDescription = null,
                                tint = IconButtonColor
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.Sort,
                                contentDescription = null,
                                tint = IconButtonColor
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = IconButtonColor
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null,
                                tint = IconButtonColor
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
                        exit = scaleOut()
                    ) {
                        Row {
                            FloatingActionButton(
                                onClick = { /*TODO*/ },
                                shape = CircleShape,
                                modifier = Modifier.padding(end = 20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = null,
                                    tint = IconColor
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
                                    tint = IconColor
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
                                    tint = IconColor
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
                            tint = IconColor,
                            modifier = Modifier.rotate(animalBooleanState * 360)
                        )
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(100.dp),
                    imageVector = Icons.Filled.Token,
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
    }
}

private const val TAG = "HomeScreen"