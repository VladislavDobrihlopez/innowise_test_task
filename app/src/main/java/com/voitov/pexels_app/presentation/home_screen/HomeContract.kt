package com.voitov.pexels_app.presentation.home_screen

import com.voitov.pexels_app.presentation.home_screen.model.CuratedUiModel
import com.voitov.pexels_app.presentation.home_screen.model.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.utils.UiText

sealed class HomeScreenUiState(
    open val curated: List<CuratedUiModel> = emptyList(),
    open val featuredCollections: List<FeaturedCollectionUiModel> = emptyList(),
    open val searchBarText: String,
    open val hasHint: Boolean,
    open val hasClearIcon: Boolean,
    open val isLoading: Boolean
) {
    data class Initial(
        override val curated: List<CuratedUiModel> = emptyList(),
        override val featuredCollections: List<FeaturedCollectionUiModel> = emptyList(),
        override val isLoading: Boolean = true,
        override val searchBarText: String = "",
        override val hasHint: Boolean = true,
        override val hasClearIcon: Boolean = false,
    ) : HomeScreenUiState(curated, featuredCollections, searchBarText, hasHint, hasClearIcon, isLoading)

    data class Failure(
        override val curated: List<CuratedUiModel> = emptyList(),
        override val featuredCollections: List<FeaturedCollectionUiModel> = emptyList(),
        override val isLoading: Boolean = false,
        override val searchBarText: String = "",
        override val hasHint: Boolean = true,
        override val hasClearIcon: Boolean = false,
    ) : HomeScreenUiState(curated, featuredCollections, searchBarText, hasHint, hasClearIcon, isLoading)

    data class Success(
        override val curated: List<CuratedUiModel> = emptyList(),
        val noResultsFound: Boolean = false,
        val isLoadingOfMorePhotosInProcess: Boolean = false,
        override val featuredCollections: List<FeaturedCollectionUiModel> = emptyList(),
        override val isLoading: Boolean = false,
        override val searchBarText: String = "",
        override val hasHint: Boolean = true,
        override val hasClearIcon: Boolean = false,
    ) : HomeScreenUiState(curated, featuredCollections, searchBarText, hasHint, hasClearIcon, isLoading)
}

sealed class HomeScreenSideEffect {
    data class ShowToast(val message: UiText) : HomeScreenSideEffect()
    data class NavigateToDetailsScreen(val photoId: Int, val query: String) : HomeScreenSideEffect()
}

sealed class HomeScreenEvent {
    data class OnClickFeaturedCollectionUiModel(val item: FeaturedCollectionUiModel) :
        HomeScreenEvent()

    data class OnSearchClick(val searchText: String) : HomeScreenEvent()
    data class OnLoadNewBunchOfPhotos(val searchBarText: String): HomeScreenEvent()
    data class OnFocusChange(val hasFocus: Boolean) : HomeScreenEvent()
    data class OnChangeSearchText(val text: String) : HomeScreenEvent()
    object OnClearClick : HomeScreenEvent()
    data class OnClickPhoto(val item: CuratedUiModel) : HomeScreenEvent()
    object OnTryAgain : HomeScreenEvent()
    object OnExplore : HomeScreenEvent()
}