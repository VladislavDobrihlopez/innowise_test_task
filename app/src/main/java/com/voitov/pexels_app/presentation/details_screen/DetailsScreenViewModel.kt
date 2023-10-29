package com.voitov.pexels_app.presentation.details_screen

import androidx.lifecycle.SavedStateHandle
import com.voitov.pexels_app.domain.AppMainSections
import com.voitov.pexels_app.navigation.AppNavScreen
import com.voitov.pexels_app.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) :
    BaseViewModel<DetailsScreenSideEffect, DetailsScreenUiState, DetailsEvent>(
        DetailsScreenUiState.Loading(showError = false)
    ) {
    private var sourceScreen: AppMainSections =
        AppMainSections.valueOf(requireNotNull(savedStateHandle[AppNavScreen.DetailsScreen.SOURCE_SCREEN_PARAM]))


    private val photoId: Int =
        requireNotNull(savedStateHandle[AppNavScreen.DetailsScreen.PHOTO_ID_PARAM])

    override fun onEvent(event: DetailsEvent) {
        TODO("Not yet implemented")
    }
}