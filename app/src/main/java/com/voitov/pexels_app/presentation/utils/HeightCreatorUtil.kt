package com.voitov.pexels_app.presentation.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun getHeightRelatedToId(id: Int) = setOfHeights[id % setOfHeights.size]

private val setOfHeights =
    listOf<Dp>(200.dp, 350.dp, 190.dp, 250.dp, 375.dp, 175.dp, 225.dp, 415.dp)
