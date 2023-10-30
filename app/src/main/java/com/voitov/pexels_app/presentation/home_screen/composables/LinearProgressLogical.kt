package com.voitov.pexels_app.presentation.home_screen.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.voitov.pexels_app.presentation.components.LinearProgress
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@Composable
fun LinearProgressLogical(isLoading: Boolean) {
    LinearProgress(
        modifier = Modifier.fillMaxWidth(),
        isActive = isLoading
    )
}