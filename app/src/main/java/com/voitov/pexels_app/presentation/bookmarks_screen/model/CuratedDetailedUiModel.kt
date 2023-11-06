package com.voitov.pexels_app.presentation.bookmarks_screen.model

import androidx.compose.ui.unit.Dp

data class CuratedDetailedUiModel(
    val id: Int,
    val url: String,
    val author: String,
    val height: Dp
)