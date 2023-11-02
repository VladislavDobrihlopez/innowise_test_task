package com.voitov.pexels_app.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.voitov.pexels_app.domain.AppMainSections
import com.voitov.pexels_app.navigation.AppNavGraph
import com.voitov.pexels_app.navigation.AppNavScreen
import com.voitov.pexels_app.navigation.AppNavigator
import com.voitov.pexels_app.navigation.rememberNavigator
import com.voitov.pexels_app.presentation.bookmarks_screen.BookmarksScreen
import com.voitov.pexels_app.presentation.details_screen.DetailsScreen
import com.voitov.pexels_app.presentation.home_screen.HomeScreen
import com.voitov.pexels_app.presentation.utils.NavigationItem
import com.voitov.pexels_app.presentation.ui.theme.Black
import com.voitov.pexels_app.presentation.ui.theme.White

@Composable
fun MainScreen(onScreenIsReady: (AppNavScreen) -> Unit) {
    val navHostController = rememberNavController()
    val navigator = rememberNavigator(navHostController)
//    var isBottomBarVisible by rememberSaveable {
//        mutableStateOf(true)
//    }

    AppNavGraph(
        navHostController = navHostController,
        homeScreen = {
            ScaffoldWrapper(
                navigator = navigator,
                navHostController = navHostController
            ) { paddings ->
                HomeScreen(
                    paddingValues = paddings,
                    onClickImageWithPhotoId = { photoId, query ->
                        navigator.navigateToDetailsScreen(
                            photoId,
                            AppMainSections.HOME_SCREEN,
                            query
                        )
                    },
                    onScreenIsReady = {
                        onScreenIsReady(AppNavScreen.HomeScreen)
                    }
                )
            }
        },
        bookmarksScreen = {
            ScaffoldWrapper(
                navigator = navigator,
                navHostController = navHostController
            ) {
                onScreenIsReady(AppNavScreen.BookmarksScreen)
                BookmarksScreen(onNavigateToHome = {
                    navigator.popToMainHomeScreen()
                }, onNavigateToDetailsScreen = {
                    navigator.navigateToDetailsScreen(it, AppMainSections.BOOKMARKS_SCREEN)
                })
            }
        },
        detailsScreen = { ->
            onScreenIsReady(AppNavScreen.DetailsScreen)
//            isBottomBarVisible = false
            BackHandler {
                navigator.popBackStack()
            }
            DetailsScreen(onNavigateBack = {
                navigator.popBackStack()
            }, onNavigateToMainScreen = {
                navigator.popToMainHomeScreen()
            })
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScaffoldWrapper(
    navigator: AppNavigator,
    navHostController: NavHostController,
    content: @Composable (PaddingValues) -> Unit,
) {
    val context = LocalContext.current

    Scaffold(bottomBar = {
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

                NavigationBarItem(
                    modifier = Modifier.fillMaxHeight(),
                    alwaysShowLabel = false,
                    onClick = {
                        if (!bottomItemIsSelected) {
                            if (navigationItem == NavigationItem.Home) {
                                navigator.navigateThroughMainScreens(AppNavScreen.HomeScreen)
                            } else {
                                navigator.navigateThroughMainScreens(AppNavScreen.BookmarksScreen)
                            }
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier.fillMaxHeight()
                        ) {
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
                                    if (bottomItemIsSelected) {
                                        if (isSystemInDarkTheme()) navigationItem.darkTheme.iconResIdOnSelectedState
                                        else navigationItem.lightTheme.iconResIdOnSelectedState
                                    } else {
                                        if (isSystemInDarkTheme())
                                            navigationItem.darkTheme.iconResIdOnUnSelectedState
                                        else
                                            navigationItem.lightTheme.iconResIdOnUnSelectedState
                                    }
                                ),
                                tint = Color.Unspecified,

                                contentDescription = navigationItem.contentDescription.getValue(
                                    context
                                )
                            )
                        }
                    },
                    selected = false,
                )
            }
        }
    }) { paddings ->
        content(paddings)
    }
}