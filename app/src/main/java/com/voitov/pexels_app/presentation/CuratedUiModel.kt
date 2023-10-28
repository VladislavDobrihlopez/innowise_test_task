package com.voitov.pexels_app.presentation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class CuratedUiModel(val id: Int, val url: String, val height: Dp) {
    companion object {
        fun getHeightInRange(minInDp: Int, maxInDp: Int) = Random.nextInt(minInDp, maxInDp).dp
    }
}