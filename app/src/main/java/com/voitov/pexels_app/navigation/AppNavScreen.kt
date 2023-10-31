package com.voitov.pexels_app.navigation

import android.net.Uri
import com.voitov.pexels_app.domain.AppMainSections

sealed class AppNavScreen(val route: String) {
    object MainScreen : AppNavScreen(MAIN_SCREEN)
    object HomeScreen : AppNavScreen(HOME_SCREEN_ROUTE)
    object BookmarksScreen : AppNavScreen(BOOKMARKS_SCREEN_ROUTE)
    object DetailsScreen : AppNavScreen(DETAILS_SCREEN_ROUTE) {
        fun passArgs(source: AppMainSections, photoId: Int, queryForThisPhoto: String) =
            DETAILS_SCREEN_ROUTE
                .replace("{$SOURCE_SCREEN_PARAM}", source.name)
                .replace("{$QUERY}", queryForThisPhoto.encode())
                .replace("{$PHOTO_ID_PARAM}", photoId.toString())

        const val SOURCE_SCREEN_PARAM = "source"
        const val PHOTO_ID_PARAM = "photo_id"
        const val QUERY = "search_query"
    }

    companion object {
        const val MAIN_SCREEN = "main_screen"
        const val HOME_SCREEN_ROUTE = "home_screen"
        const val DETAILS_SCREEN_ROUTE =
            "details_screen/{${DetailsScreen.SOURCE_SCREEN_PARAM}}/?{${DetailsScreen.QUERY}}/{${DetailsScreen.PHOTO_ID_PARAM}}"
        const val BOOKMARKS_SCREEN_ROUTE = "bookmarks_screen"
    }
}

fun String.encode(): String {
    return Uri.encode(this)
}

fun String.decode(): String {
    return Uri.decode(this)
}

