package com.voitov.pexels_app.presentation.home_screen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.domain.usecases.GetCuratedPhotosUseCase
import com.voitov.pexels_app.domain.usecases.GetFeaturedCollectionsUseCase
import com.voitov.pexels_app.presentation.BaseViewModel
import com.voitov.pexels_app.presentation.home_screen.models.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.mapper.UiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mapper: UiMapper,
    private val curatedPhotosUseCase: GetCuratedPhotosUseCase,
    private val featuredCollectionsUseCase: GetFeaturedCollectionsUseCase
) :
    BaseViewModel<HomeScreenSideEffect, HomeScreenUiState, HomeScreenEvent>(
        HomeScreenUiState.Initial()
//            featuredCollections = listOf(
//                FeaturedCollectionUiModel(
//                    "1",
//                    "Cats",
//                    isSelected = true
//                ),
//                FeaturedCollectionUiModel(
//                    "2",
//                    "Dogs",
//                    isSelected = false
//                )
//            ),
//            curated = listOf(
//                Photo(
//                    1,
//                    "https://pl-coding.com/wp-content/uploads/2022/04/laptop-cropped-2048x1415.png"
//                ),
//                Photo(
//                    2,
//                    "https://sun9-52.userapi.com/impg/wLSLAqqABOZg9i7JR2GRY2nrMm3B6oz0xF4JjA/9BNzCUopb20.jpg?size=2560x2560&quality=95&sign=3ad3077c269096ecb9370c758ea5f6f2&type=album"
//                ),
//                Photo(
//                    3,
//                    "https://sun54-1.userapi.com/impg/n_42w54LZZ5684eediCy2Obio13rAyhDK-reug/CA1VMTwdSlI.jpg?size=1728x2160&quality=96&sign=456a1b64d8af83588fc67dec75b42d10&type=album"
//                ),
//                Photo(
//                    4,
//                    "https://sun9-52.userapi.com/impg/wLSLAqqABOZg9i7JR2GRY2nrMm3B6oz0xF4JjA/9BNzCUopb20.jpg?size=2560x2560&quality=95&sign=3ad3077c269096ecb9370c758ea5f6f2&type=album"
//                ),
//                Photo(
//                    5,
//                    "https://pl-coding.com/wp-content/uploads/2022/04/laptop-cropped-2048x1415.png"
//                ),
//                Photo(
//                    62,
//                    "https://sun9-52.userapi.com/impg/wLSLAqqABOZg9i7JR2GRY2nrMm3B6oz0xF4JjA/9BNzCUopb20.jpg?size=2560x2560&quality=95&sign=3ad3077c269096ecb9370c758ea5f6f2&type=album"
//                ),
//                Photo(
//                    63,
//                    "https://sun54-1.userapi.com/impg/n_42w54LZZ5684eediCy2Obio13rAyhDK-reug/CA1VMTwdSlI.jpg?size=1728x2160&quality=96&sign=456a1b64d8af83588fc67dec75b42d10&type=album"
//                ),
//                Photo(
//                    64,
//                    "https://sun9-52.userapi.com/impg/wLSLAqqABOZg9i7JR2GRY2nrMm3B6oz0xF4JjA/9BNzCUopb20.jpg?size=2560x2560&quality=95&sign=3ad3077c269096ecb9370c758ea5f6f2&type=album"
//                ),
//            )
    ) {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        sendSideEffect(HomeScreenSideEffect.ShowToast("Shit"))
        updateState(
            HomeScreenUiState.FailureInternetIssues(
                hasHint = state.value.hasHint,
                searchBarText = state.value.searchBarText,
                hasClearIcon = state.value.hasClearIcon
            )
        )
    }

    init {
        viewModelScope.launch(exceptionHandler) {
            receiveScreenData()
        }
    }

    private suspend fun receiveScreenData() {
        supervisorScope {
            var state = HomeScreenUiState.Success()
            val photosJob = async {
                curatedPhotosUseCase()
            }

            val featuredJob = async {
                featuredCollectionsUseCase()
            }

            val photos = photosJob.await()
            val featured = featuredJob.await()

            photos.onSuccess {
                state = state.copy(curated = it.map {
                    mapper.mapDomainToUiModel(
                        it,
                        MIN_HEIGHT_OF_STAGGERED_ITEM_IN_DP,
                        MAX_HEIGHT_OF_STAGGERED_ITEM_IN_DP
                    )
                })
                Log.d(TAG, it.toString())
            }

            featured.onSuccess {
                state = state.copy(featuredCollections = it.map {
                    mapper.mapDomainToUiModel(it)
                })
            }

            updateState(state)

            if (photos.isFailure || featured.isFailure) {
                updateState(HomeScreenUiState.FailureInternetIssues())
            }
        }
    }

    private fun receiveCuratedPhotos() {
//        viewModelScope.launch(exceptionHandler) {
//            val photosJob = async {
//                curatedPhotosUseCase()
//            }
//
//            val photos = photosJob.await()
//
//            photos.onSuccess {
//                state = state.copy(curated = it.map {
//                    mapper.mapDomainToUiModel(
//                        it,
//                        MIN_HEIGHT_OF_STAGGERED_ITEM_IN_DP,
//                        MAX_HEIGHT_OF_STAGGERED_ITEM_IN_DP
//                    )
//                })
//                Log.d(TAG, it.toString())
//            }
//        }
    }

    private val _searchOnInternetEventContainer = MutableSharedFlow<String>()

    @OptIn(FlowPreview::class)
    private val search = flow<String> {
        _searchOnInternetEventContainer.debounce(TIME_INTERVAL_IN_MILLIS).collect {
            emit(it)
        }
    }
        .cancellable()
        .onEach { query ->
            Log.d(TAG, query)
        }
        .launchIn(viewModelScope)

    override fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.OnClickCurated -> handleOnClickCurated(event.item)
            is HomeScreenEvent.OnClickFeaturedCollectionUiModel -> handleOnClickFeaturedCollection(
                event.item
            )

            HomeScreenEvent.OnExplore -> handleOnExploreClick()
            HomeScreenEvent.OnTryAgain -> handleOnTryAgainClick()
            HomeScreenEvent.OnClearClick -> handleOnClearClick()
            is HomeScreenEvent.OnChangeSearchText -> handleOnChangeSearchText(event.text)
            is HomeScreenEvent.OnFocusChange -> handleFocusChange(event.hasFocus)
            is HomeScreenEvent.OnSearchClick -> handleOnSearchClick(event.searchText)
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
                        it.copy(isSelected = false)
                    }
                })
            newState as HomeScreenUiState
        }
    }

    private fun handleOnChangeSearchText(newText: String) {
        reduceState { state ->
            when (state) {
                is HomeScreenUiState.FailureInternetIssues -> state.copy(
                    searchBarText = newText,
                    hasHint = false,
                    hasClearIcon = newText.isNotEmpty()
                )

                is HomeScreenUiState.Initial -> state.copy(
                    searchBarText = newText,
                    hasHint = false,
                    hasClearIcon = newText.isNotEmpty()
                )

                is HomeScreenUiState.Success -> state.copy(
                    searchBarText = newText,
                    hasHint = false,
                    hasClearIcon = newText.isNotEmpty()
                )
            }
        }

        if (newText.isNotEmpty()) {
            viewModelScope.launch {
                Log.d(TAG, "emitted")
                _searchOnInternetEventContainer.emit(newText)
            }
        }
    }

    private fun handleFocusChange(hasFocus: Boolean) {
        reduceState { state ->
            val shouldDisplayHint = !hasFocus && state.searchBarText.isBlank()
            val searchBarText = if (shouldDisplayHint) "" else state.searchBarText
            when (state) {
                is HomeScreenUiState.FailureInternetIssues -> state.copy(
                    searchBarText = searchBarText,
                    hasHint = shouldDisplayHint
                )

                is HomeScreenUiState.Initial -> state.copy(
                    searchBarText = searchBarText,
                    hasHint = shouldDisplayHint
                )

                is HomeScreenUiState.Success -> state.copy(
                    searchBarText = searchBarText,
                    hasHint = shouldDisplayHint
                )
            }
        }
    }

    private fun handleOnClearClick() {
        reduceState { state ->
            when (state) {
                is HomeScreenUiState.FailureInternetIssues -> state.copy(
                    searchBarText = "",
                    hasClearIcon = false
                )

                is HomeScreenUiState.Initial -> state.copy(searchBarText = "", hasClearIcon = false)
                is HomeScreenUiState.Success -> state.copy(searchBarText = "", hasClearIcon = false)
            }
        }
    }

    private fun handleOnSearchClick(searchText: String) {
        val changeStatus = { state: HomeScreenUiState, newIsLoading: Boolean ->
            when (state) {
                is HomeScreenUiState.FailureInternetIssues -> state.copy(isLoading = newIsLoading)
                is HomeScreenUiState.Initial -> state.copy(isLoading = newIsLoading)
                is HomeScreenUiState.Success -> state.copy(isLoading = newIsLoading)
            }
        }
        viewModelScope.launch(exceptionHandler) {
            reduceState { state ->
                changeStatus(state, true)
            }

//            receiveScreenData()

            reduceState { state ->
                changeStatus(state, false)
            }
        }
    }

    private fun handleOnExploreClick() {
        viewModelScope.launch {
            reduceState { state ->
                when (state) {
                    is HomeScreenUiState.FailureInternetIssues -> state.copy(
                        isLoading = true,
                        searchBarText = "",
                        hasHint = true,
                        hasClearIcon = false
                    )

                    is HomeScreenUiState.Initial -> state.copy(
                        isLoading = true,
                        searchBarText = "",
                        hasHint = true,
                        hasClearIcon = false
                    )

                    is HomeScreenUiState.Success -> state.copy(
                        isLoading = true,
                        searchBarText = "",
                        hasHint = true,
                        hasClearIcon = false
                    )
                }
            }
            // todo
        }
    }

    private fun handleOnTryAgainClick() {
        viewModelScope.launch(exceptionHandler) {
            reduceState { state ->
                (state as HomeScreenUiState.FailureInternetIssues).copy(isLoading = true)
            }
            receiveScreenData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val TIME_INTERVAL_IN_MILLIS = 1000L
        private const val MIN_HEIGHT_OF_STAGGERED_ITEM_IN_DP = 150
        private const val MAX_HEIGHT_OF_STAGGERED_ITEM_IN_DP = 400
    }
}