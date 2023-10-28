package com.voitov.pexels_app.presentation.home_screen.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@Composable
fun LinearProgressLogical(isLoading: Boolean) {
    val spacing = LocalSpacing.current

    if (isLoading) {
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
    }

    com.voitov.pexels_app.presentation.components.LinearProgress(
        modifier = Modifier.fillMaxWidth(),
        isActive = isLoading
    )
}