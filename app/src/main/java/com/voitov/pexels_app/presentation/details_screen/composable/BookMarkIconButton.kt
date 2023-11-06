package com.voitov.pexels_app.presentation.details_screen.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.component.ActionBar
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun BookMarkIconButton(
    isBookmarked: Boolean,
    onBookmarkIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ActionBar(
        modifier = modifier,
        icon = if (isBookmarked) {
            if (isSystemInDarkTheme())
                R.drawable.bookmarks_selected_dark
            else
                R.drawable.bookmarks_selected_light
        } else {
            if (isSystemInDarkTheme())
                R.drawable.bookmarks_unselected_dark
            else
                R.drawable.bookmarks_unselected_light
        },
        iconContainerColor = MaterialTheme.colorScheme.secondary,
        iconTintColor = Color.Unspecified,
        onIconClick = onBookmarkIconClick,
        iconContentDescription = stringResource(id = R.string.download)
    )
}

@Preview
@Composable
private fun PreviewBookMarkIconButton_light() {
    Pexels_appTheme(darkTheme = false) {
        BookMarkIconButton(isBookmarked = true, onBookmarkIconClick = {})
    }
}

@Preview
@Composable
private fun PreviewBookMarkIconButton_dark() {
    Pexels_appTheme(darkTheme = true) {
        BookMarkIconButton(isBookmarked = false, onBookmarkIconClick = {})
    }
}