package com.voitov.pexels_app.presentation.details_screen.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.components.StubNoData
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@Composable
fun Failure(onExploreClick: () -> Unit, modifier: Modifier = Modifier) {
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
            style = MaterialTheme.typography.bodyLarge
        )
    }
}