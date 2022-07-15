package com.eynnzerr.memorymarkdown.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.eynnzerr.memorymarkdown.UriUtils
import com.eynnzerr.memorymarkdown.ui.home.HomeScreen
import com.eynnzerr.memorymarkdown.ui.home.HomeViewModel
import com.eynnzerr.memorymarkdown.ui.read.ReadScreen
import com.eynnzerr.memorymarkdown.ui.read.ReadViewModel
import com.eynnzerr.memorymarkdown.ui.write.WriteScreen
import com.eynnzerr.memorymarkdown.ui.write.WriteViewModel

object Destinations {
    const val HOME_ROUTE = "home"
    const val WRITE_ROUTE = "write"
    const val READ_ROUTE = "read"
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
            val writeViewModel: WriteViewModel = hiltViewModel()
            WriteScreen(
                navController = navHostController,
                viewModel = writeViewModel
            )
        }
        composable(
            Destinations.READ_ROUTE
            //route = Destinations.READ_ROUTE + "/{encodedUri}",
            // arguments = listOf( navArgument("encodedUri") { type = NavType.StringType } )
        ) {
            // TODO 支持阅读本地文件（Uri）和本应用创建过的文件（Room）
//            val decodedUri = Uri.decode(it.arguments?.getString("encodedUri"))
//            val uri = Uri.parse(decodedUri)
//            Log.d(TAG, "NavGraph: uri received is: $uri")
            val readViewModel = hiltViewModel<ReadViewModel>().apply {
                loadMarkdown(UriUtils.uri)
            }
            ReadScreen(
                navController = navHostController,
                viewModel = readViewModel
            )
        }
    }
}

private const val TAG = "NavGraph"