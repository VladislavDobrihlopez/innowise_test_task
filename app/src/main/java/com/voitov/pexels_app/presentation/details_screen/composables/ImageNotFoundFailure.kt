package com.voitov.pexels_app.presentation.details_screen.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.components.StubNoData
import com.voitov.pexels_app.presentation.ui.LocalSpacing
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun ImageNotFoundFailure(onExploreClick: () -> Unit, modifier: Modifier = Modifier) {
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
            text = stringResource(R.string.image_not_found),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewImageNotFound_light() {
    Pexels_appTheme(darkTheme = false) {
        ImageNotFoundFailure(onExploreClick = { })
    }
}

@Preview
@Composable
private fun PreviewImageNotFound_dark() {
    Pexels_appTheme(darkTheme = true) {
        ImageNotFoundFailure(onExploreClick = { })
    }
}