package com.voitov.pexels_app.presentation.details_screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun NavBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Icon(
            tint = MaterialTheme.colorScheme.onSecondary,
            imageVector = ImageVector.vectorResource(R.drawable.arrow_back),
            contentDescription = stringResource(R.string.navigate_back)
        )
    }
}

@Preview
@Composable
private fun PreviewNavBackButton_light() {
    Pexels_appTheme(darkTheme = false) {
        NavBackButton(onClick = {})
    }
}

@Preview
@Composable
private fun PreviewNavBackButton_dark() {
    Pexels_appTheme(darkTheme = true) {
        NavBackButton(onClick = {})
    }
}


