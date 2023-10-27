package com.voitov.pexels_app.presentation.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ProjectDimensions(
    val default: Dp = 16.dp,
    val spaceSmall: Dp = 12.dp,
    val spaceMedium: Dp = 24.dp,
    val spaceLarge: Dp = 32.dp,
)

val LocalSpacing = staticCompositionLocalOf {
    ProjectDimensions()
}