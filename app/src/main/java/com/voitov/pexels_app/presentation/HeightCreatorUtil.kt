package com.voitov.pexels_app.presentation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun getHeightRelatedToId(id: Int) = setOfHeights[id % setOfHeights.size]

private val setOfHeights =
    listOf<Dp>(200.dp, 350.dp, 150.dp, 250.dp, 450.dp, 175.dp, 225.dp)
