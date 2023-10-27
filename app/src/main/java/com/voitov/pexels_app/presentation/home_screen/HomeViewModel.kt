package com.voitov.pexels_app.presentation.home_screen

import androidx.lifecycle.viewModelScope
import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.presentation.BaseViewModel
import com.voitov.pexels_app.presentation.home_screen.models.FeaturedCollectionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :
    BaseViewModel<HomeScreenSideEffect, HomeScreenUiState, HomeScreenEvent>(
        HomeScreenUiState.InitialNoCachedData()
    ) {
    init {

    }

    override fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.OnClickCurated -> handleOnClickCurated(event.item)
            is HomeScreenEvent.OnClickFeaturedCollectionUiModel -> handleOnClickFeaturedCollection(
                event.item
            )

            HomeScreenEvent.OnExplore -> handleOnExploreClick()
            HomeScreenEvent.OnTryAgain -> handleOnTryAgainClick()
            HomeScreenEvent.OnClearClick -> handleOnClearClick()
            HomeScreenEvent.OnSearchClick -> handleOnSearchClick()
        }
    }

    private fun handleOnClickCurated(item: Photo) {
        sendSideEffect(HomeScreenSideEffect.NavigateToDetailsScreen(item.id))
    }

    private fun handleOnClickFeaturedCollection(item: FeaturedCollectionUiModel) {
        reduceState { state ->
            val successState = state as HomeScreenUiState.Success
            val newState =
                successState.copy(featuredCollections = successState.featuredCollections.map {
                    if (it == item) {
                        it.copy(isSelected = !item.isSelected)
                    } else {
                        it
                    }
                })
            newState as HomeScreenUiState
        }
    }

    private fun handleOnClearClick() {
        updateState { state ->
            when (state) {
                is HomeScreenUiState.FailureNoInternetAndCachedData -> state.copy(searchBarText = "")
                is HomeScreenUiState.InitialNoCachedData -> state.copy(searchBarText = "")
                is HomeScreenUiState.Success -> state.copy(searchBarText = "")
            }
        }
    }

    private fun handleOnSearchClick() {
        val changeStatus = { state: HomeScreenUiState, newIsLoading: Boolean ->
            when (state) {
                is HomeScreenUiState.FailureNoInternetAndCachedData -> state.copy(isLoading = newIsLoading)
                is HomeScreenUiState.InitialNoCachedData -> state.copy(isLoading = newIsLoading)
                is HomeScreenUiState.Success -> state.copy(isLoading = newIsLoading)
            }
        }
        viewModelScope.launch {
            updateState { state ->
                changeStatus(state, true)
            }
            //todo request

            updateState { state ->
                changeStatus(state, false)
            }
        }
    }

    private fun handleOnExploreClick() {
        viewModelScope.launch {
            reduceState { state ->
                (state as HomeScreenUiState.InitialNoCachedData).copy(isLoading = true)
            }
            // todo
        }
    }

    private fun handleOnTryAgainClick() {
        viewModelScope.launch {
            reduceState { state ->
                (state as HomeScreenUiState.FailureNoInternetAndCachedData).copy(isLoading = true)
            }
            // todo
        }
    }
}