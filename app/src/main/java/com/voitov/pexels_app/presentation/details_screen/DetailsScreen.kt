package com.voitov.pexels_app.presentation.details_screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DetailsScreen(viewModel: DetailsScreenViewModel = hiltViewModel(), onNavigateBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DetailsContent(
        state,
        onBack = {
            viewModel.onEvent(DetailsEvent.OnNavigateBack)
        },
        onRetryReceivingData = {
            viewModel.onEvent(DetailsEvent.OnRetryReceivingPhoto)
        },
        onBookmarkPhoto = {

        }, onDownloadPhoto = {

        },
    )
    SideEffects(viewModel = viewModel, onNavigate = onNavigateBack)
}

@Composable
private fun SideEffects(viewModel: DetailsScreenViewModel, onNavigate: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                DetailsScreenSideEffect.NavigateToPreviousScreen -> onNavigate()
                is DetailsScreenSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message.getValue(context), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}