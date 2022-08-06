package com.eynnzerr.memorymarkdown.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.eynnzerr.memorymarkdown.ui.about.AboutScreen
import com.eynnzerr.memorymarkdown.ui.about.AboutViewModel
import com.eynnzerr.memorymarkdown.utils.UriUtils
import com.eynnzerr.memorymarkdown.ui.home.HomeScreen
import com.eynnzerr.memorymarkdown.ui.home.HomeViewModel
import com.eynnzerr.memorymarkdown.ui.search.SearchScreen
import com.eynnzerr.memorymarkdown.ui.search.SearchViewModel
import com.eynnzerr.memorymarkdown.ui.setting.SettingScreen
import com.eynnzerr.memorymarkdown.ui.setting.SettingViewModel
import com.eynnzerr.memorymarkdown.ui.write.WriteScreen
import com.eynnzerr.memorymarkdown.ui.write.WriteViewModel

object Destinations {
    const val HOME_ROUTE = "home"
    const val WRITE_ROUTE = "write"
    const val SETTING_ROUTE = "setting"
    const val ABOUT_ROUTE = "about"
    const val SEARCH_ROUTE = "search"
}

@ExperimentalFoundationApi
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
            val homeViewModel = hiltViewModel<HomeViewModel>().apply {
                registerCollector()
            }
            HomeScreen(
                navController = navHostController,
                viewModel = homeViewModel
            )
        }
        composable(
            route = Destinations.WRITE_ROUTE + "/{dataId}",
            arguments = listOf(
                navArgument("dataId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            ),
            deepLinks = listOf(navDeepLink { mimeType = "text/markdown" })
        ) {
            val id = it.arguments?.getInt("dataId")!!
            val writeViewModel = hiltViewModel<WriteViewModel>().apply {
                // id != -1 indicates reading file from database. Load via Room.
                if (id != -1) loadMarkdown(id)
                // id == -1 & valid uri indicates reading file from SAF/deep link. Load via uri.
                else if (UriUtils.isUriValid) loadMarkdown(UriUtils.uri)
                // else indicates creating new file. Load via craft.
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
        composable(Destinations.ABOUT_ROUTE) {
            val aboutViewModel: AboutViewModel = hiltViewModel()
            AboutScreen(
                navigateBack = { navHostController.popBackStack() },
                viewModel = aboutViewModel
            )
        }
        composable(Destinations.SEARCH_ROUTE) {
            val searchViewModel: SearchViewModel = hiltViewModel()
            SearchScreen(
                viewModel = searchViewModel,
                navController = navHostController
            )
        }
    }
}

private const val TAG = "NavGraph"