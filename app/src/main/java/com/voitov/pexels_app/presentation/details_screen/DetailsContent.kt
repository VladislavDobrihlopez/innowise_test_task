package com.voitov.pexels_app.presentation.details_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.voitov.pexels_app.presentation.components.PhotoCard
import com.voitov.pexels_app.presentation.details_screen.composables.Failure
import com.voitov.pexels_app.presentation.home_screen.composables.LinearProgressLogical
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsContent(
    uiState: DetailsScreenUiState,
    onBack: () -> Unit,
    onRetryReceivingData: () -> Unit,
    onBookmarkPhoto: () -> Unit,
    onDownloadPhoto: () -> Unit
) {
    val spacing = LocalSpacing.current
    var topBarText by rememberSaveable {
        mutableStateOf("")
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

        }) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (uiState) {
                DetailsScreenUiState.Failure -> {
                    Failure(
                        modifier = Modifier.fillMaxSize(),
                        onExploreClick = onRetryReceivingData
                    )
                }

                is DetailsScreenUiState.Loading -> {
                    LinearProgressLogical(isLoading = true)
                    if (uiState.showError) {
                        Failure(
                            modifier = Modifier.fillMaxSize(),
                            onExploreClick = onRetryReceivingData
                        )
                    }
                }

                is DetailsScreenUiState.Success -> {
                    topBarText = uiState.details.author
                    PhotoCard(
                        modifier = Modifier.fillMaxSize(),
                        imageUrl = uiState.details.url,
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }

    }
}