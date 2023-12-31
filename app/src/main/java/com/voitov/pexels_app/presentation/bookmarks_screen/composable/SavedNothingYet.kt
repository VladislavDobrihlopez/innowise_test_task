package com.voitov.pexels_app.presentation.bookmarks_screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.component.StubNoData
import com.voitov.pexels_app.presentation.ui.LocalSpacing
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun SavedNothingYet(onExploreClick: () -> Unit, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    StubNoData(
        modifier = modifier
            .padding(
                start = spacing.spaceMedium,
                end = spacing.spaceMedium,
                bottom = spacing.spaceMedium,
                top = 40.dp
            ),
        actionText = stringResource(id = R.string.explore),
        onTextButtonClick = onExploreClick
    ) {
        Text(
            text = stringResource(R.string.you_haven_t_saved_anything),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
private fun PreviewSavedNothingYet_light() {
    Pexels_appTheme(darkTheme = false) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SavedNothingYet(onExploreClick = { })
        }
    }
}

@Preview
@Composable
private fun PreviewSavedNothingYet_dark() {
    Pexels_appTheme(darkTheme = true) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SavedNothingYet(onExploreClick = { })
        }
    }
}