package com.voitov.pexels_app.presentation.details_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DetailsScreen(
    viewModel: DetailsScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToMainScreen: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Log.d("EMISSION", state.toString())
    DetailsContent(
        uiState = state,
        onBack = {
            viewModel.onEvent(DetailsEvent.OnNavigateBack)
        },
        onExplore = {
            viewModel.onEvent(DetailsEvent.OnExplore)
        },
        onBookmarkPhoto = {
            viewModel.onEvent(DetailsEvent.OnBookmarkPhoto)
        },
        onDownloadPhoto = {
            viewModel.onEvent(DetailsEvent.OnDownloadPhoto)
        },
        onImageRenderFailed = {
            viewModel.onEvent(DetailsEvent.OnLoadingImageFailed)
        }
    )
    SideEffects(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        onNavigateToMainScreen = onNavigateToMainScreen
    )
}

@Composable
private fun SideEffects(
    viewModel: DetailsScreenViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMainScreen: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                DetailsScreenSideEffect.NavigateToPreviousScreen -> onNavigateBack()
                is DetailsScreenSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message.getValue(context), Toast.LENGTH_LONG)
                        .show()
                }

                DetailsScreenSideEffect.NavigateToHomeScreen -> onNavigateToMainScreen()
            }
        }
    }
}