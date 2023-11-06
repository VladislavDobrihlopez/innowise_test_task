package com.voitov.pexels_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.voitov.pexels_app.domain.AppMainSections

class AppNavigator(val navHostController: NavHostController) {
    fun navigateThroughMainScreens(screen: AppNavScreen) {
        navHostController.currentBackStackEntry?.let {
            if (it.lifecycleIsResumed()) {
                navHostController.navigate(screen.route) {
                    popUpTo(navHostController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
                }
            }
        }
    }

    fun navigateToDetailsScreen(
        photoId: Int,
        sourceScreen: AppMainSections,
        query: String? = null
    ) {
        navHostController.currentBackStackEntry?.let {
            if (it.lifecycleIsResumed()) {
                if (query != null) {
                    navHostController.navigate(
                        AppNavScreen.DetailsScreen.passArgs(
                            sourceScreen,
                            photoId,
                            query
                        )
                    ) {
                        launchSingleTop = true
                    }
                } else {
                    navHostController.navigate(
                        AppNavScreen.DetailsScreen.passArgs(
                            sourceScreen,
                            photoId
                        )
                    ) {
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    fun popToMainHomeScreen() {
        navHostController.currentBackStackEntry?.let {
            if (it.lifecycleIsResumed()) {
                navHostController.navigate(AppNavScreen.HomeScreen.route) {
                    restoreState = true
                    popUpTo(AppNavScreen.HomeScreen.route)
                }
            }
        }
    }

    fun popBackStack() {
        navHostController.currentBackStackEntry?.let {
            if (it.lifecycleIsResumed()) {
                navHostController.popBackStack()
            }
        }
    }
}

@Composable
fun rememberNavigator(navHostController: NavHostController = rememberNavController()): AppNavigator {
    return remember {
        AppNavigator(navHostController)
    }
}

fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED