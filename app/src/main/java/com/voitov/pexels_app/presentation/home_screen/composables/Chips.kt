package com.voitov.pexels_app.presentation.home_screen.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.presentation.components.Chip
import com.voitov.pexels_app.presentation.home_screen.models.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@Composable
fun Chips(
    featuredCollections: List<FeaturedCollectionUiModel>,
    onClick: (FeaturedCollectionUiModel) -> Unit
) {
    val spacing = LocalSpacing.current
    if (featuredCollections.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            contentPadding = PaddingValues(horizontal = spacing.spaceMedium)
        ) {
            items(items = featuredCollections, key = { it.id }) {
                Chip(isSelected = it.isSelected, text = it.title, onClick = {
                    onClick(it)
                })
            }
        }
    }
}