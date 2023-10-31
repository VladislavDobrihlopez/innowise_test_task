package com.voitov.pexels_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.voitov.pexels_app.domain.AppMainSections

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    homeScreen: @Composable () -> Unit,
    bookmarksScreen: @Composable () -> Unit,
    detailsScreen: @Composable () -> Unit
) {
    NavHost(startDestination = AppNavScreen.MainScreen.route, navController = navHostController) {
        navigation(
            startDestination = AppNavScreen.HomeScreen.route,
            route = AppNavScreen.MainScreen.route
        ) {
            composable(route = AppNavScreen.HomeScreen.route) {
                homeScreen()
            }
            composable(route = AppNavScreen.BookmarksScreen.route) {
                bookmarksScreen()
            }
        }
        composable(
            route = AppNavScreen.DetailsScreen.route,
            arguments = listOf(navArgument(name = AppNavScreen.DetailsScreen.SOURCE_SCREEN_PARAM) {
                type = NavType.StringType
            }, navArgument(name = AppNavScreen.DetailsScreen.QUERY) {
                type = NavType.StringType
                defaultValue = ""
            }, navArgument(name = AppNavScreen.DetailsScreen.PHOTO_ID_PARAM) {
                type = NavType.IntType
            })
        ) {
            detailsScreen()
        }
    }
}