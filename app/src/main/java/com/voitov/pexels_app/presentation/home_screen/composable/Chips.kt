package com.voitov.pexels_app.presentation.home_screen.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.presentation.component.Chip
import com.voitov.pexels_app.presentation.home_screen.model.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.ui.LocalSpacing
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Chips(
    featuredCollections: List<FeaturedCollectionUiModel>,
    onClick: (FeaturedCollectionUiModel) -> Unit
) {
    val state = rememberLazyListState()
    val items = featuredCollections.sortedBy { !it.isSelected }
    LaunchedEffect(key1 = items) {
        if (items.isNotEmpty()) {
            state.animateScrollToItem(0)
        }
    }
    val spacing = LocalSpacing.current
    if (featuredCollections.isNotEmpty()) {
        LazyRow(
            state = state,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            contentPadding = PaddingValues(horizontal = spacing.spaceMedium)
        ) {
            items(items = items, key = { it.id }) {
                Chip(
                    modifier = Modifier.animateItemPlacement(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (it.isSelected) {
                            FontWeight.W700
                        } else {
                            FontWeight.W400
                        }
                    ),
                    isSelected = it.isSelected,
                    text = it.title,
                    onClick = {
                        onClick(it)
                    })
            }
        }
    }
}

@Preview
@Composable
private fun PreviewChips_light() {
    Pexels_appTheme(darkTheme = false) {
        Chips(featuredCollections = listOf(
            FeaturedCollectionUiModel(
                id = "finibus", title = "idque", isSelected = false
            ),
            FeaturedCollectionUiModel(
                id = "finibus", title = "idque", isSelected = false
            ),
            FeaturedCollectionUiModel(
                id = "finibus", title = "idque", isSelected = false
            )
        ), onClick = {})
    }
}

@Preview
@Composable
private fun PreviewChips_dark() {
    Pexels_appTheme(darkTheme = true) {
        Chips(featuredCollections = listOf(
            FeaturedCollectionUiModel(
                id = "finibus", title = "idque", isSelected = false
            ),
            FeaturedCollectionUiModel(
                id = "finibus", title = "idque", isSelected = false
            ),
            FeaturedCollectionUiModel(
                id = "finibus", title = "idque", isSelected = false
            )
        ), onClick = {})
    }
}