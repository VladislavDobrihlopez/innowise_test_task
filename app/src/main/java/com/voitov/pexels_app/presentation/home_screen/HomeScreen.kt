package com.voitov.pexels_app.presentation.home_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    onClickImageWithPhotoId: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    Log.d("BaseViewModel", "comp: $screenState")

    HomeContent(
        paddingValues = paddingValues,
        uiState = screenState,
        onSearchBarTextChange = {
            viewModel.onEvent(HomeScreenEvent.OnChangeSearchText(it))
        },
        onFocusChange = {
            viewModel.onEvent(HomeScreenEvent.OnFocusChange(it.hasFocus))
        },
        onStartSearching = {
            viewModel.onEvent(HomeScreenEvent.OnSearchClick(it))
        },
        onClear = {
            viewModel.onEvent(HomeScreenEvent.OnClearClick)
        },
        onExplore = {
            viewModel.onEvent(HomeScreenEvent.OnExplore)
        },
        onTryAgain = {
            viewModel.onEvent(HomeScreenEvent.OnTryAgain)
        },
        onClickedChipItem = {
            viewModel.onEvent(HomeScreenEvent.OnClickFeaturedCollectionUiModel(it))
        },
        onPhotoClick = {
            viewModel.onEvent(HomeScreenEvent.OnClickCurated(it))
        },
        onEndOfPhotosFeed = {
            viewModel.onEvent(HomeScreenEvent.LoadNewBunchOfPhotos(it))
        }
    )
    SideEffects(viewModel = viewModel, onNavigate = onClickImageWithPhotoId)
}

@Composable
private fun SideEffects(
    viewModel: HomeViewModel,
    onNavigate: (Int) -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is HomeScreenSideEffect.NavigateToDetailsScreen -> onNavigate(effect.photoId)
                is HomeScreenSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message.getValue(context), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}