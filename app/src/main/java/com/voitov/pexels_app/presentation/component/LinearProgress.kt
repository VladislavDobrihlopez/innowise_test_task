package com.voitov.pexels_app.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayDarkShade
import com.voitov.pexels_app.presentation.ui.theme.GrayLightShade
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun LinearProgress(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    trackColor: Color = if (isSystemInDarkTheme()) DarkGrayDarkShade else GrayLightShade,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
) {
    AnimatedVisibility(visible = isActive) {
        LinearProgressIndicator(
            modifier = modifier,
            color = backgroundColor,
            trackColor = trackColor,
        )
    }
}

@Preview
@Composable
private fun PreviewLinearProgress_light() {
    Pexels_appTheme(darkTheme = false) {
        LinearProgress(isActive = true)
    }
}

@Preview
@Composable
private fun PreviewLinearProgress_dark() {
    Pexels_appTheme(darkTheme = true) {
        LinearProgress(isActive = true)
    }
}