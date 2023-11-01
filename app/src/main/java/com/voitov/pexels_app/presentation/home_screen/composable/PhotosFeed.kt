package com.voitov.pexels_app.presentation.home_screen.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.CuratedUiModel
import com.voitov.pexels_app.presentation.component.PhotoCard
import com.voitov.pexels_app.presentation.component.StubNoData
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotosFeed(
    isPaginationInProgress: Boolean,
    staggeredGridState: LazyStaggeredGridState,
    onExploreClick: () -> Unit,
    onPhotoCardClick: (CuratedUiModel) -> Unit,
    curated: List<CuratedUiModel> = emptyList(),
    noResultsFound: Boolean = false,
    onEndOfList: () -> Unit
) {
    val spacing = LocalSpacing.current
    Spacer(modifier = Modifier.height(spacing.spaceMedium))
    if (noResultsFound) {
        StubNoData(
            modifier = Modifier.fillMaxSize(),
            actionText = stringResource(R.string.explore),
            onTextButtonClick = onExploreClick
        ) {
            Text(
                text = stringResource(R.string.no_results_found),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyVerticalStaggeredGrid(
            contentPadding = PaddingValues(horizontal = spacing.spaceSmall),
            state = staggeredGridState,
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = spacing.spaceSmall,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(items = curated, key = { it.id }) {
                PhotoCard(
                    modifier = Modifier
                        .height(it.height)
                        .fillMaxSize()
                        .animateItemPlacement()
                        .animateContentSize(),
                    imageUrl = it.url,
                    onClick = {
                        onPhotoCardClick(it)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
                if (!isPaginationInProgress) {
                    SideEffect {
                        onEndOfList()
                    }
                }
            }
        }
    }
}