package com.voitov.pexels_app.presentation.home_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayDarkShade
import com.voitov.pexels_app.presentation.ui.theme.GrayLightShade

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