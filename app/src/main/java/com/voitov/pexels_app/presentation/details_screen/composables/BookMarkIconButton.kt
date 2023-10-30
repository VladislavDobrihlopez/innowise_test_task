package com.voitov.pexels_app.presentation.details_screen.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.components.ActionBar
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun BookMarkIconButton(onBookmarkIconClick: () -> Unit) {
    ActionBar(
        icon = R.drawable.bookmark_detailed_screen,
        iconContainerColor = MaterialTheme.colorScheme.secondary,
        iconTintColor = MaterialTheme.colorScheme.onSecondary,
        onIconClick = onBookmarkIconClick,
        iconContentDescription = "download"
    )
}

@Preview
@Composable
private fun PreviewBookMarkIconButton_light() {
    Pexels_appTheme(darkTheme = false) {
        BookMarkIconButton {}
    }
}

@Preview
@Composable
private fun PreviewBookMarkIconButton_dark() {
    Pexels_appTheme(darkTheme = true) {
        BookMarkIconButton {}
    }
}