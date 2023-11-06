package com.voitov.pexels_app.presentation.bookmarks_screen.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.presentation.bookmarks_screen.model.CuratedDetailedUiModel
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkedPhotosFeed(
    isPaginationInProgress: Boolean,
    staggeredGridState: LazyStaggeredGridState,
    onPhotoCardClick: (CuratedDetailedUiModel) -> Unit,
    curated: List<CuratedDetailedUiModel> = emptyList(),
    onEndOfList: () -> Unit
) {
    val spacing = LocalSpacing.current
    LazyVerticalStaggeredGrid(
        state = staggeredGridState,
        contentPadding = PaddingValues(
            start = spacing.spaceMedium,
            end = spacing.spaceMedium,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
        ),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = spacing.spaceSmall,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        items(items = curated, key = { it.id }) {
            PhotoCardWithAuthor(
                modifier = Modifier
                    .height(it.height)
                    .fillMaxSize()
                    .animateItemPlacement(),
                imageUrl = it.url,
                author = it.author,
                onRenderFailed = {},
                onClick = {
                    onPhotoCardClick(it)
                }
            )
        }

        item {
            if (!isPaginationInProgress) {
                SideEffect {
                    onEndOfList()
                }
            }
        }
    }
}