package com.eynnzerr.memorymarkdown.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eynnzerr.memorymarkdown.utils.UriUtils
import com.eynnzerr.memorymarkdown.ui.home.HomeScreen
import com.eynnzerr.memorymarkdown.ui.home.HomeViewModel
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
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                navController = navHostController,
                viewModel = homeViewModel
            )
        }
        composable(Destinations.WRITE_ROUTE) {
            val writeViewModel = hiltViewModel<WriteViewModel>().apply {
                if (UriUtils.isUriValid) loadMarkdown(UriUtils.uri)
                else loadCraft()
            }
            WriteScreen(
                navController = navHostController,
                viewModel = writeViewModel
            )
        }
//        composable(Destinations.READ_ROUTE) {
//            val readViewModel = hiltViewModel<ReadViewModel>().apply {
//                if (UriUtils.isUriValid) loadMarkdown(UriUtils.uri)
//            }
//            ReadScreen(
//                navController = navHostController,
//                viewModel = readViewModel
//            )
//        }
    }
}

private const val TAG = "NavGraph"