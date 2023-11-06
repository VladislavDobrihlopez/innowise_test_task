package com.voitov.pexels_app.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.voitov.pexels_app.presentation.ui.theme.Black
import com.voitov.pexels_app.presentation.ui.theme.White
import com.voitov.pexels_app.presentation.utils.NavigationItem

@Composable
fun MainScreen(onScreenIsReady: (AppNavScreen) -> Unit) {
    val navHostController = rememberNavController()
    val navigator = rememberNavigator(navHostController)

    AppNavGraph(
        navHostController = navHostController,
        homeScreen = {
            ScaffoldWrapper(
                navigator = navigator,
            ) {
                HomeScreen(
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
    content: @Composable (PaddingValues) -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            // bottom bar uses 12 dp up and 16 dp down in material 3 by default
            NavigationBar(
                tonalElevation = 0.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height((48f).dp)
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

                    Column(
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (bottomItemIsSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp, 2.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }

                        this@NavigationBar.NavigationBarItem(
                            modifier = Modifier,
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
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp),
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
                            },
                            selected = false,
                        )
                    }
                }
            }
        }) { paddings ->
        content(paddings)
    }
}