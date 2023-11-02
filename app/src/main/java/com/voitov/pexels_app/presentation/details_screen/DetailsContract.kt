package com.voitov.pexels_app.presentation.details_screen

import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.presentation.utils.UiText

sealed class DetailsScreenUiState {
    object Loading: DetailsScreenUiState()
    data class Success(val details: PhotoDetails): DetailsScreenUiState()
    object Failure: DetailsScreenUiState()
}

sealed class DetailsScreenSideEffect {
    data class ShowToast(val message: UiText) : DetailsScreenSideEffect()
    object NavigateToPreviousScreen : DetailsScreenSideEffect()
    object NavigateToHomeScreen: DetailsScreenSideEffect()
}

sealed class DetailsEvent {
    object OnDownloadPhoto: DetailsEvent()
    object OnNavigateBack: DetailsEvent()
    object OnBookmarkPhoto: DetailsEvent()
    object OnExplore: DetailsEvent()
    object OnLoadingImageFailed: DetailsEvent()
}