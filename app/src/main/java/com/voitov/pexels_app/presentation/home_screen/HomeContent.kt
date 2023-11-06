package com.voitov.pexels_app.presentation.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.presentation.component.SearchBar
import com.voitov.pexels_app.presentation.home_screen.composable.Chips
import com.voitov.pexels_app.presentation.home_screen.composable.LinearProgressLogical
import com.voitov.pexels_app.presentation.home_screen.composable.PhotosFeed
import com.voitov.pexels_app.presentation.home_screen.composable.StubNoInternet
import com.voitov.pexels_app.presentation.home_screen.model.CuratedUiModel
import com.voitov.pexels_app.presentation.home_screen.model.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.ui.LocalSpacing
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

private const val ZERO_REQUEST = ""
private const val FIRST_GRID_ITEM = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeScreenUiState,
    onScreenIsReady: () -> Unit,
    onSearchBarTextChange: (String) -> Unit,
    onFocusChange: (FocusState) -> Unit,
    onStartSearching: (String) -> Unit,
    onClear: () -> Unit,
    onExplore: () -> Unit,
    onTryAgain: () -> Unit,
    onPhotoClick: (CuratedUiModel) -> Unit,
    onClickedChipItem: (FeaturedCollectionUiModel) -> Unit,
    onEndOfPhotosFeed: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    val gridState = rememberLazyStaggeredGridState()
    var lastSearchBarText by rememberSaveable {
        mutableStateOf(ZERO_REQUEST)
    }

    LaunchedEffect(key1 = uiState) {
        with(uiState) {
            if (lastSearchBarText != searchBarText) {
                gridState.animateScrollToItem(FIRST_GRID_ITEM)
                lastSearchBarText = searchBarText
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .padding(top = spacing.spaceSmall)
                .padding(
                    bottom = it.calculateBottomPadding(),
                    start = it.calculateStartPadding(LayoutDirection.Ltr),
                    end = it.calculateEndPadding(LayoutDirection.Ltr)
                )
                .fillMaxSize()
        ) {
            SearchBar(
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = spacing.spaceMedium),
                text = uiState.searchBarText,
                onValueChange = onSearchBarTextChange,
                onFocusChange = onFocusChange,
                onSearch = onStartSearching,
                onClear = onClear,
                shouldShowHint = uiState.hasHint,
                shouldShowClearIcon = uiState.hasClearIcon
            )

            when (uiState) {
                is HomeScreenUiState.Failure -> {
                    if (uiState.featuredCollections.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        Chips(
                            featuredCollections = uiState.featuredCollections,
                            onClick = onClickedChipItem
                        )
                    }
                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    }
                    LinearProgressLogical(isLoading = uiState.isLoading)
                    StubNoInternet(onTryAgainClick = onTryAgain)
                    onScreenIsReady()
                }

                is HomeScreenUiState.Initial -> {
                    LinearProgressLogical(isLoading = uiState.isLoading)
                }

                is HomeScreenUiState.Success -> {
                    if (uiState.featuredCollections.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        Chips(
                            featuredCollections = uiState.featuredCollections,
                            onClick = onClickedChipItem
                        )
                    }
                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    }
                    LinearProgressLogical(isLoading = uiState.isLoading)
                    PhotosFeed(
                        isPaginationInProgress = uiState.isLoadingOfMorePhotosInProcess,
                        curated = uiState.curated,
                        staggeredGridState = gridState,
                        noResultsFound = uiState.noResultsFound,
                        onExploreClick = onExplore,
                        onPhotoCardClick = onPhotoClick,
                        onEndOfList = {
                            onEndOfPhotosFeed(uiState.searchBarText)
                        }
                    )
                    onScreenIsReady()
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomeContent_light() {
    Pexels_appTheme(darkTheme = false) {
        HomeContent(
            uiState = HomeScreenUiState.Success(),
            onSearchBarTextChange = {},
            onFocusChange = {},
            onStartSearching = {},
            onClear = {},
            onExplore = {},
            onTryAgain = {},
            onClickedChipItem = {},
            onPhotoClick = {},
            onEndOfPhotosFeed = {},
            onScreenIsReady = {},
        )
    }
}

@Preview
@Composable
private fun PreviewHomeContent_dark() {
    Pexels_appTheme(darkTheme = true) {
        HomeContent(
            uiState = HomeScreenUiState.Success(),
            onSearchBarTextChange = {},
            onFocusChange = {},
            onStartSearching = {},
            onClear = {},
            onExplore = {},
            onTryAgain = {},
            onClickedChipItem = {},
            onPhotoClick = {},
            onEndOfPhotosFeed = {},
            onScreenIsReady = {},
        )
    }
}