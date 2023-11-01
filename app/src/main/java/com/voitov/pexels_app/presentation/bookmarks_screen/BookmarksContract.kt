package com.voitov.pexels_app.presentation.bookmarks_screen

import com.voitov.pexels_app.presentation.bookmarks_screen.model.CuratedDetailedUiModel
import com.voitov.pexels_app.presentation.utils.UiText

sealed class BookmarksScreenUiState {
    object Loading : BookmarksScreenUiState()
    object Failure : BookmarksScreenUiState()
    data class Success(
        val photos: List<CuratedDetailedUiModel> = emptyList(),
        val isPaginationInProgress: Boolean = false
    ) : BookmarksScreenUiState()
}

sealed class BookmarksEvent {
    data class OnClickPhoto(val photo: CuratedDetailedUiModel) : BookmarksEvent()
    object OnClickExplore : BookmarksEvent()
    object OnLoadNewBunchOfPhotos : BookmarksEvent()
}

sealed class BookmarksScreenSideEffect {
    data class ShowToast(val uiText: UiText) : BookmarksScreenSideEffect()
    data class NavigateToDetailsScreen(val photoId: Int) : BookmarksScreenSideEffect()
    object NavigateToHomeMainScreen : BookmarksScreenSideEffect()
}