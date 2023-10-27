package com.voitov.pexels_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.voitov.pexels_app.domain.AppMainSections

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    homeScreen: @Composable () -> Unit,
    bookmarksScreen: @Composable () -> Unit,
    detailsScreen: @Composable (AppMainSections, Int) -> Unit
) {
    NavHost(startDestination = AppNavScreen.HomeScreen.route, navController = navHostController) {
        composable(route = AppNavScreen.HomeScreen.route) {
            homeScreen()
        }
        composable(route = AppNavScreen.BookmarksScreen.route) {
            bookmarksScreen()
        }
        composable(
            route = AppNavScreen.DetailsScreen.route,
            arguments = listOf(navArgument(name = AppNavScreen.DetailsScreen.SOURCE_SCREEN_PARAM) {
                type = NavType.EnumType(AppMainSections::class.java)
            }, navArgument(name = AppNavScreen.DetailsScreen.PHOTO_ID_PARAM) {
                type = NavType.IntType
            })
        ) {
            val sourceScreenName = it.arguments?.getString(AppNavScreen.DetailsScreen.SOURCE_SCREEN_PARAM) ?: throw IllegalStateException()
            val photoId = it.arguments?.getInt(AppNavScreen.DetailsScreen.PHOTO_ID_PARAM) ?: throw IllegalStateException()
            val sourceName = AppMainSections.valueOf(sourceScreenName)
            detailsScreen(sourceName, photoId)
        }
    }
}