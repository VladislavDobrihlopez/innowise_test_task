package com.voitov.pexels_app.presentation.details_screen.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import com.voitov.pexels_app.presentation.component.PhotoCard
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

@Composable
fun ZoomablePhoto(scrollState: ScrollState, onImageRenderFailed: () -> Unit, imgUrl: String) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val scope = rememberCoroutineScope()

        var scale by remember {
            mutableFloatStateOf(1f)
        }

        var offset by remember {
            mutableStateOf(Offset.Zero)
        }

        val animatedScale by animateFloatAsState(
            targetValue = scale,
            label = "anim_scale"
        )

        val animatedOffset by animateOffsetAsState(
            targetValue = offset,
            label = "anim_offset"
        )

        PhotoCard(
            onRenderFailed = onImageRenderFailed,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false, onClick = {})
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                    translationX = animatedOffset.x
                    translationY = animatedOffset.y
                }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        do {
                            val event =
                                awaitPointerEvent()

                            if (event.type == PointerEventType.Release) {
                                scale = 1f
                                offset = Offset.Zero

                                scope.launch {
                                    scrollState.setScrolling(true)
                                }

                                event.changes.forEach { it.consume() }
                            } else {
                                scale =
                                    (scale * event.calculateZoom()).coerceIn(
                                        1f,
                                        5f
                                    )
                                val widthOutOfView =
                                    (scale - 1) * constraints.maxWidth
                                val heightOutOfView =
                                    (scale - 1) * constraints.maxHeight
                                val maxX = widthOutOfView / 2
                                val maxY = heightOutOfView / 2
                                val offsetX =
                                    (offset.x + scale * event.calculatePan().x).coerceIn(
                                        -maxX,
                                        maxX
                                    )
                                val offsetY =
                                    (offset.y + scale * event.calculatePan().y).coerceIn(
                                        -maxY,
                                        maxY
                                    )
                                offset = Offset(offsetX, offsetY)

                                if (scale > 1f) {
                                    scope.launch {
                                        scrollState.setScrolling(false)
                                    }
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                },
            imageUrl = imgUrl,
            contentScale = ContentScale.FillWidth
        )
    }
}

suspend fun ScrollState.setScrolling(value: Boolean) {
    scroll(scrollPriority = MutatePriority.PreventUserInput) {
        when (value) {
            true -> Unit
            else -> awaitCancellation()
        }
    }
}