package com.eynnzerr.memorymarkdown.ui.read

//@ExperimentalMaterial3Api
//@Composable
//fun ReadScreen(
//    navController: NavHostController,
//    viewModel: ReadViewModel
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    Scaffold(
//        topBar = {
//            SmallTopAppBar(
//                modifier = Modifier.statusBarsPadding(),
//                title = {},
//                navigationIcon = {
//                    IconButton(onClick = {
//                        // empty uri message before quitting
//                        UriUtils.run {
//                            if (isUriValid) {
//                                uri = null
//                                isUriValid = false
//                            }
//                        }
//                        navController.navigateTo(Destinations.HOME_ROUTE)
//                    }) {
//                        Icon(
//                            imageVector = Icons.Filled.ArrowBack,
//                            contentDescription = null,
//                            tint = IconButtonColor
//                        )
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                        // TODO 携带当前页面内容跳转到编辑页面。如果当前是Uri，则传递Uri；如果当前是Room数据，则传递文件名即可，之后更新Room表项及重写本地文件
//                    }) {
//                        Icon(
//                            imageVector = Icons.Filled.Edit,
//                            contentDescription = null,
//                            tint = IconButtonColor
//                        )
//                    }
//                }
//            )
//        },
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(it)
//        ) {
//            Text(
//                text = uiState.title,
//                style = MaterialTheme.typography.headlineLarge,
//                modifier = Modifier.padding(horizontal = 13.dp),
//                maxLines = 1
//            )
//            AndroidView(
//                factory = { context ->
//                    TextView(context).also { textView ->
//                        Log.d(TAG, "ReadScreen: content is: ${uiState.content}")
//                        viewModel.getMarkwon().setMarkdown(textView, uiState.content)
//                    }
//                },
//                modifier = Modifier.padding(13.dp),
//                update = { textView ->
//                    viewModel.getMarkwon().setMarkdown(textView, uiState.content)
//                }
//            )
//        }
//    }
//}
//
//private const val TAG = "ReadScreen"