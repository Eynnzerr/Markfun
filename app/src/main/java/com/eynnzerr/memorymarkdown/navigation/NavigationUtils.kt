package com.eynnzerr.memorymarkdown.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.navigateTo(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToSingle(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

private const val TAG = "NavigationUtils"