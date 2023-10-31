package com.voitov.pexels_app.presentation.utils

import com.voitov.pexels_app.R
import com.voitov.pexels_app.navigation.AppNavScreen

sealed class NavigationItem(
    val contentDescription: UiText,
    val lightTheme: ThemedBunch,
    val darkTheme: ThemedBunch,
    val screen: AppNavScreen
) {
    object Home : NavigationItem(
        contentDescription = UiText.Resource(R.string.home_section),
        lightTheme = ThemedBunch(
            iconResIdOnSelectedState = R.drawable.home_selected_light,
            iconResIdOnUnSelectedState = R.drawable.home_unselected_light
        ),
        darkTheme = ThemedBunch(
            iconResIdOnSelectedState = R.drawable.home_selected_dark,
            iconResIdOnUnSelectedState = R.drawable.home_unselected_dark
        ),
        screen = AppNavScreen.HomeScreen
    )

    object Bookmarks : NavigationItem(
        contentDescription = UiText.Resource(R.string.bookmarks_section),
        lightTheme = ThemedBunch(
            iconResIdOnSelectedState = R.drawable.bookmarks_selected_light,
            iconResIdOnUnSelectedState = R.drawable.bookmarks_unselected_light
        ),
        darkTheme = ThemedBunch(
            iconResIdOnSelectedState = R.drawable.bookmarks_selected_dark,
            iconResIdOnUnSelectedState = R.drawable.bookmarks_unselected_dark
        ),
        screen = AppNavScreen.BookmarksScreen
    )

    companion object {
        data class ThemedBunch(
            val iconResIdOnSelectedState: Int,
            val iconResIdOnUnSelectedState: Int
        )
    }
}