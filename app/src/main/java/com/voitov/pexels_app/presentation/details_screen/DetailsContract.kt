package com.voitov.pexels_app.presentation.details_screen

import com.voitov.pexels_app.domain.models.PhotoDetails
import com.voitov.pexels_app.presentation.utils.UiText

sealed class DetailsScreenUiState {
    data class Loading(val showError: Boolean): DetailsScreenUiState()
    data class Success(val details: PhotoDetails): DetailsScreenUiState()
    object Failure: DetailsScreenUiState()
}

sealed class DetailsScreenSideEffect {
    data class ShowToast(val message: UiText) : DetailsScreenSideEffect()
    object NavigateToPreviousScreen : DetailsScreenSideEffect()
}

sealed class DetailsEvent {
    object OnDownloadPhoto: DetailsEvent()
    object OnNavigateBack: DetailsEvent()
    object OnBookmarkPhoto: DetailsEvent()
    object OnRetryReceivingPhoto: DetailsEvent()
}