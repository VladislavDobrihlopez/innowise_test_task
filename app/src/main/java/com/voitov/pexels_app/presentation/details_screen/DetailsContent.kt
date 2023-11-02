package com.voitov.pexels_app.presentation.details_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.component.ActionBar
import com.voitov.pexels_app.presentation.details_screen.composable.BookMarkIconButton
import com.voitov.pexels_app.presentation.details_screen.composable.ImageNotFoundFailure
import com.voitov.pexels_app.presentation.details_screen.composable.NavBackButton
import com.voitov.pexels_app.presentation.details_screen.composable.TopBar
import com.voitov.pexels_app.presentation.details_screen.composable.ZoomablePhoto
import com.voitov.pexels_app.presentation.home_screen.composable.LinearProgressLogical
import com.voitov.pexels_app.presentation.ui.LocalSpacing

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

                DetailsScreenUiState.Loading -> {
                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    LinearProgressLogical(isLoading = true)
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
                            onIconClick = {},
                            shouldShowLabel = true,
                            label = stringResource(id = R.string.download)
                        )
                        BookMarkIconButton(
                            isBookmarked = false,
                            onBookmarkIconClick = {},
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
                        ZoomablePhoto(
                            scrollState = lazyState,
                            onImageRenderFailed = onImageRenderFailed,
                            imgUrl = uiState.details.sourceUrl
                        )
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
                                onBookmarkIconClick = onBookmarkPhoto,
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val NO_DATA = ""