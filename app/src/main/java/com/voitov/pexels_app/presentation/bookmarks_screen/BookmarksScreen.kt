package com.voitov.pexels_app.presentation.bookmarks_screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel = hiltViewModel(),
    onNavigateToDetailsScreen: (Int) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BookmarksContent(
        uiState = state,
        onExplore = {
            viewModel.onEvent(BookmarksEvent.OnClickExplore)
        },
        onClickPhoto = {
            viewModel.onEvent(BookmarksEvent.OnClickPhoto(it))
        },
        onEndOfPhotosFeed = {
            viewModel.onEvent(BookmarksEvent.OnLoadNewBunchOfPhotos)
        },
    )
    SideEffects(
        viewModel = viewModel,
        onNavigateToDetailsScreen = onNavigateToDetailsScreen,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
private fun SideEffects(
    viewModel: BookmarksViewModel,
    onNavigateToDetailsScreen: (Int) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is BookmarksScreenSideEffect.NavigateToDetailsScreen -> onNavigateToDetailsScreen(
                    effect.photoId
                )

                is BookmarksScreenSideEffect.ShowToast -> Toast.makeText(
                    context,
                    effect.uiText.getValue(context),
                    Toast.LENGTH_LONG
                ).show()

                BookmarksScreenSideEffect.NavigateToHomeMainScreen ->
                    onNavigateToHome()
            }
        }
    }
}