package com.voitov.pexels_app.presentation.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voitov.pexels_app.presentation.home_screen.components.Chip
import com.voitov.pexels_app.presentation.home_screen.components.LinearProgress
import com.voitov.pexels_app.presentation.home_screen.components.SearchBar
import com.voitov.pexels_app.presentation.ui.LocalSpacing
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun HomeContent(paddingValues: PaddingValues, uiState: HomeScreenUiState) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(vertical = spacing.spaceSmall)
    ) {
        SearchBar(
            modifier = Modifier.padding(horizontal = spacing.spaceMedium),
            text = "",
            onValueChange = {},
            onFocusChange = {},
            onSearch = {},
            onClear = {},
        )
        when (uiState) {
            is HomeScreenUiState.FailureNoInternetAndCachedData -> {

            }

            is HomeScreenUiState.InitialNoCachedData -> {

            }

            is HomeScreenUiState.Success -> {

            }
        }
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            contentPadding = PaddingValues(horizontal = spacing.spaceMedium)
        ) {
            item {
                Chip(isSelected = true, text = "Cats")
            }
            item {
                Chip(isSelected = false, text = "Pikachu")
            }
            item {
                Chip(isSelected = false, text = "Pikachu")
            }
            item {
                Chip(isSelected = false, text = "Dogs")
            }
            item {
                Chip(isSelected = false, text = "Dogs")
            }
        }
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        LinearProgress(modifier = Modifier.fillMaxWidth(), isActive = true)
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(120.dp),
            verticalItemSpacing = spacing.spaceSmall,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {

        }
    }
}

@Preview
@Composable
private fun PreviewHomeContent_light() {
    Pexels_appTheme(darkTheme = false) {
        HomeContent(PaddingValues(0.dp), uiState = HomeScreenUiState.Success())
    }
}

@Preview
@Composable
private fun PreviewHomeContent_dark() {
    Pexels_appTheme(darkTheme = true) {
        HomeContent(PaddingValues(0.dp), uiState = HomeScreenUiState.Success())
    }
}