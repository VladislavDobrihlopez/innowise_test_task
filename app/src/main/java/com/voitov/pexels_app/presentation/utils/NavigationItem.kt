package com.voitov.pexels_app.presentation.utils

import com.voitov.pexels_app.R
import com.voitov.pexels_app.navigation.AppNavScreen

sealed class NavigationItem(
    val contentDescription: UiText,
    val iconResIdOnSelectedState: Int,
    val iconResIdOnUnSelectedState: Int,
    val screen: AppNavScreen
) {
    object Home : NavigationItem(
        contentDescription = UiText.Resource(R.string.home_section),
        iconResIdOnSelectedState = R.drawable.home_selected,
        iconResIdOnUnSelectedState = R.drawable.home_unselected,
        screen = AppNavScreen.HomeScreen
    )

    object Bookmarks : NavigationItem(
        contentDescription = UiText.Resource(R.string.bookmarks_section),
        iconResIdOnSelectedState = R.drawable.bookmarks_selected,
        iconResIdOnUnSelectedState = R.drawable.bookmarks_unselected,
        screen = AppNavScreen.BookmarksScreen
    )
}