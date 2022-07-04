package com.eynnzerr.memorymarkdown.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eynnzerr.memorymarkdown.ui.home.HomeScreen
import com.eynnzerr.memorymarkdown.ui.write.WriteScreen
import com.eynnzerr.memorymarkdown.ui.write.WriteViewModel

object Destinations {
    const val HOME_ROUTE = "home"
    const val WRITE_ROUTE = "write"
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun NavGraph(
    navHostController: NavHostController,
    startDestination: String = Destinations.HOME_ROUTE
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        route = "root"
    ) {
        composable(Destinations.HOME_ROUTE) {
            HomeScreen(
                navController = navHostController
            )
        }
        composable(Destinations.WRITE_ROUTE) {
            val writeViewModel: WriteViewModel = hiltViewModel()
            val uiState by writeViewModel.uiState.collectAsState()
            WriteScreen(
                navController = navHostController,
                editor = writeViewModel.getEditor(),
                uiState = uiState,
                optionList = writeViewModel.optionList,
                onStateChange = writeViewModel::updateUiState,
                saveCraft = writeViewModel::saveCraft,
                removeCraft = writeViewModel::emptyCraft,
                saveFile = writeViewModel::saveMarkdown
            )
        }
    }
}