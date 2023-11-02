package com.voitov.pexels_app.presentation.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.presentation.CuratedUiModel
import com.voitov.pexels_app.presentation.component.SearchBar
import com.voitov.pexels_app.presentation.home_screen.composable.Chips
import com.voitov.pexels_app.presentation.home_screen.composable.LinearProgressLogical
import com.voitov.pexels_app.presentation.home_screen.composable.PhotosFeed
import com.voitov.pexels_app.presentation.home_screen.composable.StubNoInternet
import com.voitov.pexels_app.presentation.home_screen.model.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.ui.LocalSpacing
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    uiState: HomeScreenUiState,
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
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(vertical = spacing.spaceSmall)
    ) {
        SearchBar(
            modifier = Modifier.padding(horizontal = spacing.spaceMedium),
            text = uiState.searchBarText,
            onValueChange = onSearchBarTextChange,
            onFocusChange = onFocusChange,
            onSearch = onStartSearching,
            onClear = onClear,
            shouldShowHint = uiState.hasHint,
            shouldShowClearIcon = uiState.hasClearIcon
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        when (uiState) {
            is HomeScreenUiState.Failure -> {
                Chips(
                    featuredCollections = uiState.featuredCollections,
                    onClick = onClickedChipItem
                )
                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                }
                LinearProgressLogical(isLoading = uiState.isLoading)
                StubNoInternet(onTryAgainClick = onTryAgain)
            }

            is HomeScreenUiState.Initial -> {
                LinearProgressLogical(isLoading = uiState.isLoading)
            }

            is HomeScreenUiState.Success -> {
                val staggeredGridState = rememberLazyStaggeredGridState()

                LaunchedEffect(key1 = uiState.isLoading) {
                    if (uiState.curated.isNotEmpty()) {
                        staggeredGridState.animateScrollToItem(0)
                    }
                }

                Chips(
                    featuredCollections = uiState.featuredCollections,
                    onClick = onClickedChipItem
                )
                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                }
                LinearProgressLogical(isLoading = uiState.isLoading)
                PhotosFeed(
                    isPaginationInProgress = uiState.isLoadingOfMorePhotosInProcess,
                    staggeredGridState = staggeredGridState,
                    curated = uiState.curated,
                    noResultsFound = uiState.noResultsFound,
                    onExploreClick = onExplore,
                    onPhotoCardClick = onPhotoClick,
                    onEndOfList = {
                        onEndOfPhotosFeed(uiState.searchBarText)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomeContent_light() {
    Pexels_appTheme(darkTheme = false) {
        HomeContent(
            paddingValues = PaddingValues(0.dp),
            uiState = HomeScreenUiState.Success(),
            onSearchBarTextChange = {},
            onFocusChange = {},
            onStartSearching = {},
            onClear = {},
            onExplore = {},
            onTryAgain = {},
            onClickedChipItem = {},
            onPhotoClick = {},
            onEndOfPhotosFeed = {}
        )
    }
}

@Preview
@Composable
private fun PreviewHomeContent_dark() {
    Pexels_appTheme(darkTheme = true) {
        HomeContent(
            paddingValues = PaddingValues(0.dp),
            uiState = HomeScreenUiState.Success(),
            onSearchBarTextChange = {},
            onFocusChange = {},
            onStartSearching = {},
            onClear = {},
            onExplore = {},
            onTryAgain = {},
            onClickedChipItem = {},
            onPhotoClick = {},
            onEndOfPhotosFeed = {}
        )
    }
}