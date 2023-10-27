package com.voitov.pexels_app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.voitov.pexels_app.navigation.AppNavGraph
import com.voitov.pexels_app.navigation.AppNavScreen
import com.voitov.pexels_app.navigation.rememberNavigator
import com.voitov.pexels_app.presentation.home_screen.HomeScreen
import com.voitov.pexels_app.presentation.home_screen.models.NavigationItem
import com.voitov.pexels_app.presentation.ui.theme.Black
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayLightShade
import com.voitov.pexels_app.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navHostController = rememberNavController()
    val navigator = rememberNavigator(navHostController)
    var isBottomBarVisible by rememberSaveable {
        mutableStateOf(true)
    }
    Scaffold(bottomBar = {
        NavigationBar(
            modifier = Modifier
                .shadow(elevation = 1.dp)
                .navigationBarsPadding()
                .height(64.dp),
            containerColor = if (isSystemInDarkTheme()) Black else White,
        ) {
            val navBackStackEntry =
                navigator.navHostController.currentBackStackEntryAsState()

            val items = listOf(
                NavigationItem.Home,
                NavigationItem.Bookmarks
            )

            items.forEach { navigationItem ->
                val bottomItemIsSelected =
                    navBackStackEntry.value?.destination?.hierarchy?.any {
                        it.route == navigationItem.screen.route
                    } ?: false

                NavigationBarItem(
                    alwaysShowLabel = false,
                    onClick = {
                        if (!bottomItemIsSelected) {
                            if (navigationItem == NavigationItem.Home) {
                                navigator.navigateThroughMainScreens(AppNavScreen.HomeScreen)
                            } else {
                                navigator.navigateThroughMainScreens(AppNavScreen.BookmarksScreen)
//                                navigationState.navigateTo(navigationItem.screen.route)
                            }
                        }
                    },
                    icon = {
                        Icon(
                            ImageVector.vectorResource(
                                id =
                                if (bottomItemIsSelected)
                                    navigationItem.iconResIdOnSelectedState
                                else
                                    navigationItem.iconResIdOnUnSelectedState
                            ),
                            tint = if (bottomItemIsSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Unspecified
                            },
                            contentDescription = navigationItem.contentDescription.getValue(context)
                        )
                    },
                    selected = false,
//                    selectedContentColor = MaterialTheme.colors.onPrimary,
//                    unselectedContentColor = MaterialTheme.colors.onSecondary,
                )
            }
        }
    }) { paddings ->
        AppNavGraph(
            navHostController = navHostController,
            homeScreen = {
                HomeScreen(paddings)
            },
            bookmarksScreen = {

            },
            detailsScreen = { sourceScreen, photoId ->
                isBottomBarVisible = false
            }
        )
    }
}