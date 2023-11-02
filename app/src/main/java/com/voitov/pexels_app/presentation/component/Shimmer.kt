package com.voitov.pexels_app.presentation.component

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmer(): Modifier = composed {
    var contentSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -1.5f * contentSize.width.toFloat(),
        targetValue = 1.5f * contentSize.width.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(1000)), label = ""
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.DarkGray,
                MaterialTheme.colorScheme.secondary,
                Color.Gray
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(
                startOffsetX + contentSize.width.toFloat(),
                contentSize.height.toFloat()
            )
        )
    ).onSizeChanged { contentSize = IntSize(it.width, it.height) }
}