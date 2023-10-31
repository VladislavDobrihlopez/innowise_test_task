package com.voitov.pexels_app.presentation.home_screen.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.voitov.pexels_app.presentation.component.LinearProgress

@Composable
fun LinearProgressLogical(isLoading: Boolean) {
    LinearProgress(
        modifier = Modifier.fillMaxWidth(),
        isActive = isLoading
    )
}