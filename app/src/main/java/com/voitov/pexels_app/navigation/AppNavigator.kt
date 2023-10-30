package com.voitov.pexels_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.voitov.pexels_app.domain.AppMainSections

class AppNavigator(val navHostController: NavHostController) {
    fun navigateThroughMainScreens(screen: AppNavScreen) {
        navHostController.navigate(screen.route) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                inclusive = true
                saveState = true
            }
            restoreState = true
        }
    }

    fun navigateToDetailsScreen(photoId: Int, sourceScreen: AppMainSections) {
        navHostController.navigate(AppNavScreen.DetailsScreen.passArgs(sourceScreen, photoId)) {
            launchSingleTop = true
        }
    }

    fun navigateToSourceMainScreen() {
        navHostController.navigate(AppNavScreen.HomeScreen.route) {
            popBackStack()
            restoreState = true
        }
    }

    fun popBackStack() {
        navHostController.popBackStack()
    }
}

@Composable
fun rememberNavigator(navHostController: NavHostController = rememberNavController()): AppNavigator {
    return remember {
        AppNavigator(navHostController)
    }
}