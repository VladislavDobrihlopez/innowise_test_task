package com.voitov.pexels_app.presentation.bookmarks_screen.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class CuratedDetailedUiModel(val id: Int, val url: String, val author: String, val height: Dp) {
    companion object {
        fun getHeightInRange(minInDp: Int, maxInDp: Int) = Random.nextInt(minInDp, maxInDp).dp
    }
}