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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.components.ActionBar
import com.voitov.pexels_app.presentation.components.PhotoCard
import com.voitov.pexels_app.presentation.details_screen.composables.BookMarkIconButton
import com.voitov.pexels_app.presentation.details_screen.composables.ImageNotFoundFailure
import com.voitov.pexels_app.presentation.details_screen.composables.TopBar
import com.voitov.pexels_app.presentation.home_screen.composables.LinearProgressLogical
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
                onClickBack = onBack
            )
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
                        BookMarkIconButton(onBookmarkIconClick = onBookmarkPhoto)
                    }
                }

                is DetailsScreenUiState.Success -> {
                    topBarText = uiState.details.author
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = spacing.spaceMedium, end = spacing.spaceMedium)
                            .verticalScroll(rememberScrollState())
                    ) {
                        PhotoCard(
                            onRenderFailed = onImageRenderFailed,
                            modifier = Modifier.fillMaxSize(),
                            imageUrl = uiState.details.localUrl ?: uiState.details.networkUrl,
                            contentScale = ContentScale.FillWidth
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
                            BookMarkIconButton(onBookmarkIconClick = onBookmarkPhoto)
                        }
                    }
                }
            }
        }
    }
}

private const val NO_DATA = ""