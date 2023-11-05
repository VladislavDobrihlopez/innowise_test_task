package com.voitov.pexels_app.presentation.home_screen.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.voitov.pexels_app.presentation.component.LinearProgress
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun LinearProgressLogical(isLoading: Boolean) {
    LinearProgress(
        modifier = Modifier.fillMaxWidth(),
        isActive = isLoading
    )
}

@Preview
@Composable
private fun PreviewLinearProgressLogical_light() {
    Pexels_appTheme(darkTheme = false) {
        LinearProgressLogical(isLoading = true)
    }
}

@Preview
@Composable
private fun PreviewLinearProgressLogical_dark() {
    Pexels_appTheme(darkTheme = true) {
        LinearProgressLogical(isLoading = true)
    }
}