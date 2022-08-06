package com.eynnzerr.memorymarkdown.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import com.eynnzerr.memorymarkdown.navigation.navigateToSingle
import com.eynnzerr.memorymarkdown.ui.home.HomeList
import com.eynnzerr.memorymarkdown.utils.UriUtils

@ExperimentalFoundationApi
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavHostController
) {
    val keyword by viewModel.keywordState.collectAsState()
    val resultList by viewModel.resultState.collectAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    TextField(
                        value = keyword,
                        onValueChange = { newInput ->
                            viewModel.updateKeyWord(newInput)
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.search_hint)
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            disabledTextColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (resultList.isEmpty()) {
            NotFound()
        }
        else {
            HomeList(
                modifier = Modifier
                    .padding(paddingValues)
                    .navigationBarsPadding(),
                dataList = resultList,
                markwon = viewModel.markwon,
                onStarredItem = {},
                onClickItem = { data ->
                    data.uri?.let { uri ->
                        UriUtils.prepareUri(uri)
                    }
                    navController.navigateToSingle(Destinations.WRITE_ROUTE + "/${data.id}")
                },
                onLongPressItem = {}
            )
        }
    }
}

@Composable
private fun NotFound() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.not_found),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer
        )
        Text(
            text = stringResource(id = R.string.not_found),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}