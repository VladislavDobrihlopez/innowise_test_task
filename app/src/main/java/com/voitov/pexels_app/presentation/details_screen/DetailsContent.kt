package com.voitov.pexels_app.presentation.details_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.component.ActionBar
import com.voitov.pexels_app.presentation.component.PhotoCard
import com.voitov.pexels_app.presentation.details_screen.composable.BookMarkIconButton
import com.voitov.pexels_app.presentation.details_screen.composable.ImageNotFoundFailure
import com.voitov.pexels_app.presentation.details_screen.composable.NavBackButton
import com.voitov.pexels_app.presentation.details_screen.composable.TopBar
import com.voitov.pexels_app.presentation.home_screen.composable.LinearProgressLogical
import com.voitov.pexels_app.presentation.ui.LocalSpacing
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsContent(
    uiState: DetailsScreenUiState,
    onBack: () -> Unit,
    onExplore: () -> Unit,
    onBookmarkPhoto: () -> Unit,
    onDownloadPhoto: () -> Unit,
    onImageRenderFailed: () -> Unit
) {
    val spacing = LocalSpacing.current
    var topBarText by rememberSaveable {
        mutableStateOf(NO_DATA)
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        topBar = {
            TopBar(
                titleText = topBarText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spaceMedium),
            ) {
                NavBackButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    onClick = onBack
                )
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(top = 17.dp, bottom = 8.dp)
        ) {
            when (uiState) {
                DetailsScreenUiState.Failure -> {
                    ImageNotFoundFailure(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = spacing.spaceMedium, end = spacing.spaceMedium),
                        onExploreClick = onExplore
                    )
                }

                is DetailsScreenUiState.Loading -> {
                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    LinearProgressLogical(isLoading = true)
                    if (uiState.showError) {
                        ImageNotFoundFailure(
                            modifier = Modifier.fillMaxSize(),
                            onExploreClick = onExplore
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = spacing.spaceMedium, end = spacing.spaceMedium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionBar(
                            modifier = Modifier.width(180.dp),
                            icon = R.drawable.download,
                            onIconClick = onDownloadPhoto,
                            shouldShowLabel = true,
                            label = stringResource(id = R.string.download)
                        )
                        BookMarkIconButton(
                            modifier = Modifier.clickable(enabled = false) {},
                            isBookmarked = false,
                            onBookmarkIconClick = onBookmarkPhoto
                        )
                    }
                }

                is DetailsScreenUiState.Success -> {
                    topBarText = uiState.details.author

                    val lazyState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = spacing.spaceMedium, end = spacing.spaceMedium)
                            .verticalScroll(lazyState)
                    ) {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            var scale by remember {
                                mutableFloatStateOf(1f)
                            }

                            var offset by remember {
                                mutableStateOf(
                                    Offset.Zero
                                )
                            }

                            val animatedScale by animateFloatAsState(
                                targetValue = scale,
                                label = ""
                            )

                            val animatedOffset by animateOffsetAsState(
                                targetValue = offset,
                                label = ""
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
                                                        lazyState.setScrolling(true)
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
                                                            lazyState.setScrolling(false)
                                                        }
                                                    }
                                                }
                                            } while (event.changes.any { it.pressed })
                                        }
                                    },
                                imageUrl = uiState.details.sourceUrl,
                                contentScale = ContentScale.FillWidth
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ActionBar(
                                modifier = Modifier.width(180.dp),
                                icon = R.drawable.download,
                                onIconClick = onDownloadPhoto,
                                shouldShowLabel = true,
                                label = stringResource(id = R.string.download)
                            )
                            BookMarkIconButton(
                                isBookmarked = uiState.details.isBookmarked,
                                onBookmarkIconClick = onBookmarkPhoto
                            )
                        }
                    }
                }
            }
        }
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

private const val NO_DATA = ""