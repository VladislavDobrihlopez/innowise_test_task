package com.voitov.pexels_app.presentation.home_screen

import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.presentation.home_screen.models.FeaturedCollectionUiModel

sealed class HomeScreenUiState(open val searchBarText: String) {
    data class InitialNoCachedData(
        val featuredCollections: List<FeaturedCollectionUiModel> = emptyList(),
        val isLoading: Boolean = false,
        override val searchBarText: String = ""
    ) : HomeScreenUiState(searchBarText)

    data class FailureNoInternetAndCachedData(
        val featuredCollections: List<FeaturedCollectionUiModel> = emptyList(),
        val isLoading: Boolean = false,
        override val searchBarText: String = ""
    ) : HomeScreenUiState(searchBarText)

    data class Success(
        val featuredCollections: List<FeaturedCollectionUiModel> = emptyList(),
        val curated: List<Photo> = emptyList(),
        val isLoading: Boolean = false,
        override val searchBarText: String = ""
    ) : HomeScreenUiState(searchBarText)
}

sealed class HomeScreenSideEffect {
    data class ShowToast(val message: String) : HomeScreenSideEffect()
    data class NavigateToDetailsScreen(val photoId: Int) : HomeScreenSideEffect()
}

sealed class HomeScreenEvent {
    data class OnClickFeaturedCollectionUiModel(val item: FeaturedCollectionUiModel) :
        HomeScreenEvent()

    object OnSearchClick : HomeScreenEvent()
    object OnClearClick : HomeScreenEvent()
    data class OnClickCurated(val item: Photo) : HomeScreenEvent()
    object OnTryAgain : HomeScreenEvent()
    object OnExplore : HomeScreenEvent()
}