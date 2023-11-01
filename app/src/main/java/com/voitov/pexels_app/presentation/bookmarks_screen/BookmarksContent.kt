package com.voitov.pexels_app.presentation.bookmarks_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.bookmarks_screen.composable.BookmarkedPhotosFeed
import com.voitov.pexels_app.presentation.bookmarks_screen.composable.SavedNothingYet
import com.voitov.pexels_app.presentation.bookmarks_screen.model.CuratedDetailedUiModel
import com.voitov.pexels_app.presentation.details_screen.composable.TopBar
import com.voitov.pexels_app.presentation.home_screen.composable.LinearProgressLogical
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksContent(
    uiState: BookmarksScreenUiState,
    onExplore: () -> Unit,
    onClickPhoto: (CuratedDetailedUiModel) -> Unit,
    onEndOfPhotosFeed: () -> Unit
) {
    val spacing = LocalSpacing.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        topBar = {
            TopBar(
                titleText = stringResource(R.string.bookmarks),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spaceMedium),
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(top = 17.dp, bottom = 8.dp)
        ) {
            when (uiState) {
                BookmarksScreenUiState.Failure -> {
                    SavedNothingYet(onExploreClick = onExplore, modifier = Modifier.fillMaxSize())
                }

                BookmarksScreenUiState.Loading -> {
                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    LinearProgressLogical(isLoading = true)
                }

                is BookmarksScreenUiState.Success -> {
                    val gridState = rememberLazyStaggeredGridState()
                    BookmarkedPhotosFeed(
                        isPaginationInProgress = uiState.isPaginationInProgress,
                        staggeredGridState = gridState,
                        onPhotoCardClick = onClickPhoto,
                        curated = uiState.photos,
                        onEndOfList = onEndOfPhotosFeed
                    )
                }
            }
        }
    }
}