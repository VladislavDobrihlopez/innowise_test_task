package com.voitov.pexels_app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.voitov.pexels_app.domain.AppMainSections
import com.voitov.pexels_app.navigation.AppNavGraph
import com.voitov.pexels_app.navigation.AppNavScreen
import com.voitov.pexels_app.navigation.rememberNavigator
import com.voitov.pexels_app.presentation.home_screen.HomeScreen
import com.voitov.pexels_app.presentation.utils.NavigationItem
import com.voitov.pexels_app.presentation.ui.theme.Black
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
//        Row(
//            modifier = Modifier
//                .navigationBarsPadding()
//                .height(64.dp)
////                .background(if (isSystemInDarkTheme()) Black else White)
//        ) {
        NavigationBar(
            tonalElevation = 0.dp,
            modifier = Modifier
                .navigationBarsPadding()
                .height(48.dp) // 16dp is fab spacing of scaffold
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme()) Black else White),

            containerColor = if (isSystemInDarkTheme()) Black else White,
            contentColor = Color.Transparent
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

//                Row(modifier = Modifier
//                    .weight(1f)
//                    .clickable {
//                        if (!bottomItemIsSelected) {
//                            if (navigationItem == NavigationItem.Home) {
//                                navigator.navigateThroughMainScreens(AppNavScreen.HomeScreen)
//                            } else {
//                                navigator.navigateThroughMainScreens(AppNavScreen.BookmarksScreen)
////                                navigationState.navigateTo(navigationItem.screen.route)
//                            }
//                        }
//                    }) {
//                    Icon(
//                        ImageVector.vectorResource(
//                            id =
//                            if (bottomItemIsSelected)
//                                navigationItem.iconResIdOnSelectedState
//                            else
//                                navigationItem.iconResIdOnUnSelectedState
//                        ),
//
//                        contentDescription = navigationItem.contentDescription.getValue(
//                            context
//                        )
//                    )
//                }
                NavigationBarItem(
                    modifier = Modifier.fillMaxHeight(),
//                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Red),
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
                        Box(modifier = Modifier.fillMaxHeight()) {
                            if (bottomItemIsSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp, 2.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .align(Alignment.TopCenter)
                                )
                            }
                            Icon(
                                modifier = Modifier.align(Alignment.Center),
                                imageVector = ImageVector.vectorResource(
                                    id =
                                    if (bottomItemIsSelected)
                                        navigationItem.iconResIdOnSelectedState
                                    else
                                        navigationItem.iconResIdOnUnSelectedState
                                ),
                                tint = Color.Unspecified,

                                contentDescription = navigationItem.contentDescription.getValue(
                                    context
                                )
                            )
                        }
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
                HomeScreen(paddingValues = paddings, onClickImageWithPhotoId = { photoId ->
                    navigator.navigateToDetailsScreen(photoId, AppMainSections.HOME_SCREEN)
                })
            },
            bookmarksScreen = {

            },
            detailsScreen = { sourceScreen, photoId ->
                isBottomBarVisible = false
            }
        )
    }
}