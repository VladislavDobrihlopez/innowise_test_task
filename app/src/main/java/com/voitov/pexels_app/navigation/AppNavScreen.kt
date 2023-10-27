package com.voitov.pexels_app.navigation

import android.net.Uri
import com.voitov.pexels_app.domain.AppMainSections

sealed class AppNavScreen(val route: String) {
    object HomeScreen : AppNavScreen(HOME_SCREEN_ROUTE)
    object BookmarksScreen : AppNavScreen(BOOKMARKS_SCREEN_ROUTE)
    object DetailsScreen : AppNavScreen(DETAILS_SCREEN_ROUTE) {
        fun passArgs(source: AppMainSections, photoId: Int) =
            DETAILS_SCREEN_ROUTE
                .replace(SOURCE_SCREEN_PARAM, source.name)
                .replace(PHOTO_ID_PARAM, photoId.toString())
                .encode()

        const val SOURCE_SCREEN_PARAM = "source"
        const val PHOTO_ID_PARAM = "photo_id"
    }

    companion object {
        const val HOME_SCREEN_ROUTE = "home_screen"
        const val DETAILS_SCREEN_ROUTE =
            "details_screen/{${DetailsScreen.SOURCE_SCREEN_PARAM}}/{${DetailsScreen.PHOTO_ID_PARAM}}"
        const val BOOKMARKS_SCREEN_ROUTE = "bookmarks_screen"
    }
}

private fun String.encode(): String {
    return Uri.encode(this)
}

private fun String.decode(): String {
    return Uri.decode(this)
}

