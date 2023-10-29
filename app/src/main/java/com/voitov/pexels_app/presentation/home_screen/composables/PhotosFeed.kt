package com.voitov.pexels_app.presentation.home_screen.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.presentation.CuratedUiModel
import com.voitov.pexels_app.presentation.components.PhotoCard
import com.voitov.pexels_app.presentation.components.StubNoData
import com.voitov.pexels_app.presentation.ui.LocalSpacing
import kotlin.random.Random

@Composable
fun PhotosFeed(
    curated: List<CuratedUiModel> = emptyList(),
    onExploreClick: () -> Unit,
    onPhotoCardClick: (CuratedUiModel) -> Unit,
    noResultsFound: Boolean = false,
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
            modifier = Modifier.padding(horizontal = spacing.spaceSmall),
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = spacing.spaceSmall,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(items = curated, key = { it.id }) {
                PhotoCard(
                    modifier = Modifier
                        .height(it.height)
                        .clickable(onClick = {
                            onPhotoCardClick(it)
                        }),
                    imageUrl = it.url
                )
            }
        }
    }
}