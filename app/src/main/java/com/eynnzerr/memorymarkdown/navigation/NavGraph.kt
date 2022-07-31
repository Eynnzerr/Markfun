package com.eynnzerr.memorymarkdown.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.eynnzerr.memorymarkdown.utils.UriUtils
import com.eynnzerr.memorymarkdown.ui.home.HomeScreen
import com.eynnzerr.memorymarkdown.ui.home.HomeViewModel
import com.eynnzerr.memorymarkdown.ui.setting.SettingScreen
import com.eynnzerr.memorymarkdown.ui.setting.SettingViewModel
import com.eynnzerr.memorymarkdown.ui.write.WriteScreen
import com.eynnzerr.memorymarkdown.ui.write.WriteViewModel

object Destinations {
    const val HOME_ROUTE = "home"
    const val WRITE_ROUTE = "write"
    const val SETTING_ROUTE = "setting"
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
        composable(
            Destinations.WRITE_ROUTE,
            deepLinks = listOf(navDeepLink { mimeType = "text/markdown" })
        ) {
            val writeViewModel = hiltViewModel<WriteViewModel>().apply {
                if (UriUtils.isUriValid) loadMarkdown(UriUtils.uri)
                else loadCraft()
            }
            WriteScreen(
                navController = navHostController,
                viewModel = writeViewModel
            )
        }
        composable(Destinations.SETTING_ROUTE) {
            val settingViewModel: SettingViewModel = hiltViewModel()
            SettingScreen(
                navController = navHostController,
                viewModel = settingViewModel
            )
        }
    }
}

private const val TAG = "NavGraph"