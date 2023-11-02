package com.voitov.pexels_app.presentation.home_screen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.voitov.pexels_app.R
import com.voitov.pexels_app.domain.PexelsException
import com.voitov.pexels_app.domain.usecase.GetCuratedPhotosUseCase
import com.voitov.pexels_app.domain.usecase.GetFeaturedCollectionsUseCase
import com.voitov.pexels_app.domain.usecase.RequestCollectionUseCase
import com.voitov.pexels_app.domain.usecase.RequestNextPhotosUseCase
import com.voitov.pexels_app.presentation.BaseViewModel
import com.voitov.pexels_app.presentation.CuratedUiModel
import com.voitov.pexels_app.presentation.home_screen.model.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.mapper.UiMapper
import com.voitov.pexels_app.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mapper: UiMapper,
    private val curatedPhotosUseCase: GetCuratedPhotosUseCase,
    private val featuredCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val requestFeaturedCollectionsUseCase: RequestCollectionUseCase,
    private val requestPhotosUseCase: RequestNextPhotosUseCase,
) : BaseViewModel<HomeScreenSideEffect, HomeScreenUiState, HomeScreenEvent>(
    HomeScreenUiState.Initial()
) {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Resource(R.string.unexpected_error)))
        switchState<HomeScreenUiState.Failure>(isLoading = false, noResultsFound = false)
    }

    init {
        Log.d("TEST_VIEWMODEL", this.toString())

        viewModelScope.launch(exceptionHandler) {
            launch {
                requestPhotosUseCase()
            }
            launch {
                requestFeaturedCollectionsUseCase()
            }
        }
    }

    val curatedPhotosFlow = curatedPhotosUseCase()
        .onEach {
            Log.d(TAG, "curatedPhotosUseCase: $it")

            if (it.isEmpty()) {
                switchState<HomeScreenUiState.Success>(
                    curated = emptyList(),
                    isLoading = false,
                    noResultsFound = true
                )
            } else {
                switchState<HomeScreenUiState.Success>(
                    curated = it.map { item ->
                        mapper.mapDomainToUiModel(
                            item,
                            MIN_HEIGHT_OF_STAGGERED_ITEM_IN_DP,
                            MAX_HEIGHT_OF_STAGGERED_ITEM_IN_DP
                        )
                    },
                    isLoading = false,
                    noResultsFound = false,
                    areMorePhotosIncoming = false
                )
            }
        }
        .catch { ex ->
            when (ex) {
                PexelsException.NoInternet -> {
                    sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Resource(R.string.no_internet)))
                }

                else -> {
                    sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Resource(R.string.unexpected_error)))
                }
            }
        }
        .onCompletion {
            Log.d(TAG, "completed")
        }
        .launchIn(viewModelScope)

    val a = featuredCollectionsUseCase()
        .onEach {
            Log.d(TAG, "featuredCollectionsUseCase: $it")
            switchState<HomeScreenUiState.Success>(featuredCollections = it.map {
                mapper.mapDomainToUiModel(it)
            }, isLoading = false)
        }
        .catch { ex ->
            when (ex) {
                is PexelsException.NoInternet -> {
                    sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Resource(R.string.no_internet)))
                }

                else -> {
                    sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Resource(R.string.unexpected_error)))
                }
            }
        }
        .launchIn(viewModelScope)

    private val _searchOnInternetEventContainer = MutableSharedFlow<String>()

    @OptIn(FlowPreview::class)
    private val search = flow<String> {
        _searchOnInternetEventContainer.debounce(TIME_INTERVAL_IN_MILLIS).collect {
            emit(it)
        }
    }
        .onEach { query ->
            updateCurrentState(isLoading = true)
            requestPhotosUseCase(query)
        }
        .launchIn(viewModelScope)

    override fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.OnClickPhoto -> handleOnClickCurated(event.item)
            is HomeScreenEvent.OnClickFeaturedCollectionUiModel -> handleOnClickFeaturedCollection(
                event.item
            )

            HomeScreenEvent.OnExplore -> handleOnExploreClick()
            HomeScreenEvent.OnTryAgain -> handleOnTryAgainClick()
            HomeScreenEvent.OnClearClick -> handleOnClearClick()
            is HomeScreenEvent.OnChangeSearchText -> handleOnChangeSearchText(event.text)
            is HomeScreenEvent.OnFocusChange -> handleFocusChange(event.hasFocus)
            is HomeScreenEvent.OnSearchClick -> handleOnSearchClick(event.searchText)
            is HomeScreenEvent.OnLoadNewBunchOfPhotos -> handleOnLoadNewBunchOfPhotos(event.searchBarText)
        }
    }

    private fun handleOnLoadNewBunchOfPhotos(searchBarText: String) {
        viewModelScope.launch {
            updateCurrentState(areMorePhotosIncoming = true)
            requestPhotosUseCase(searchBarText)
        }
    }

    private fun handleOnClickCurated(item: CuratedUiModel) {
        sendSideEffect(HomeScreenSideEffect.NavigateToDetailsScreen(item.id, _state.value.searchBarText))
    }

    private fun getUpdatedFeaturedCollections(predicate: (FeaturedCollectionUiModel) -> Boolean): List<FeaturedCollectionUiModel> {
        return state.value.featuredCollections.map {
            if (predicate(it)) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it.copy(isSelected = false)
            }
        }
    }

    private fun handleOnClickFeaturedCollection(item: FeaturedCollectionUiModel) {
        viewModelScope.launch {
            var updatedSearchBarText = item.title
            val updatedFeaturedCollections = getUpdatedFeaturedCollections { it == item }
            updateCurrentState(
                searchBarText = updatedSearchBarText,
                featuredCollections = updatedFeaturedCollections,
                hasHint = false,
                hasClearIcon = true,
                isLoading = true
            )
            requestPhotosUseCase(updatedSearchBarText)
        }
    }

    private fun handleOnChangeSearchText(newText: String) {
        val updatedFeaturedCollections =
            getUpdatedFeaturedCollections { it.title.lowercase() == newText.lowercase() }

        updateCurrentState(
            featuredCollections = updatedFeaturedCollections,
            searchBarText = newText,
            hasHint = false,
            hasClearIcon = newText.isNotEmpty(),
        )

        if (newText.isNotEmpty()) {
            viewModelScope.launch {
                _searchOnInternetEventContainer.emit(newText)
            }
        }
    }

    private fun handleFocusChange(hasFocus: Boolean) {
        val state = _state.value
        val shouldDisplayHint = !hasFocus && state.searchBarText.isBlank()
        val searchBarText = if (shouldDisplayHint) "" else state.searchBarText
        updateCurrentState(
            searchBarText = searchBarText,
            hasHint = shouldDisplayHint
        )
    }

    private fun handleOnClearClick() {
        updateCurrentState(
            searchBarText = "",
            hasClearIcon = false,
        )
    }

    private fun handleOnSearchClick(searchText: String) {
        viewModelScope.launch(exceptionHandler) {
            updateCurrentState(isLoading = true)
            requestPhotosUseCase(searchText)
            //updateCurrentState(isLoading = false)
        }
    }

    private fun handleOnExploreClick() {
        viewModelScope.launch(exceptionHandler) {
            require(_state.value.curated.isEmpty())

            updateCurrentState(
                isLoading = true,
                searchBarText = "",
                hasClearIcon = false
            )
            requestPhotosUseCase()
        }
    }

    private fun handleOnTryAgainClick() {
        viewModelScope.launch(exceptionHandler) {
            reduceState { state ->
                (state as HomeScreenUiState.Failure).copy(isLoading = true)
            }
            if (state.value.featuredCollections.isEmpty()) {
                requestFeaturedCollectionsUseCase()
            }
            if (state.value.curated.isEmpty()) {
                requestPhotosUseCase()
            }
        }
    }

    private fun updateCurrentState(
        curated: List<CuratedUiModel>? = null,
        featuredCollections: List<FeaturedCollectionUiModel>? = null,
        searchBarText: String? = null,
        hasHint: Boolean? = null,
        hasClearIcon: Boolean? = null,
        isLoading: Boolean? = null,
        noResultsFound: Boolean? = null,
        areMorePhotosIncoming: Boolean? = null
    ) {
        reduceState { state ->
            when (state) {
                is HomeScreenUiState.Failure -> {
                    state.copy(
                        curated = curated ?: state.curated,
                        featuredCollections = featuredCollections ?: state.featuredCollections,
                        searchBarText = searchBarText ?: state.searchBarText,
                        hasHint = hasHint ?: state.hasHint,
                        hasClearIcon = hasClearIcon ?: state.hasClearIcon,
                        isLoading = isLoading ?: state.isLoading
                    )
                }

                is HomeScreenUiState.Initial -> {
                    state.copy(
                        curated = curated ?: state.curated,
                        featuredCollections = featuredCollections ?: state.featuredCollections,
                        searchBarText = searchBarText ?: state.searchBarText,
                        hasHint = hasHint ?: state.hasHint,
                        hasClearIcon = hasClearIcon ?: state.hasClearIcon,
                        isLoading = isLoading ?: state.isLoading
                    )
                }

                is HomeScreenUiState.Success -> {
                    state.copy(
                        curated = curated ?: state.curated,
                        featuredCollections = featuredCollections ?: state.featuredCollections,
                        searchBarText = searchBarText ?: state.searchBarText,
                        hasHint = hasHint ?: state.hasHint,
                        hasClearIcon = hasClearIcon ?: state.hasClearIcon,
                        isLoading = isLoading ?: state.isLoading,
                        isLoadingOfMorePhotosInProcess = areMorePhotosIncoming
                            ?: state.isLoadingOfMorePhotosInProcess
                    )
                }
            }
        }
    }

    private inline fun <reified T : HomeScreenUiState> switchState(
        curated: List<CuratedUiModel>? = null,
        featuredCollections: List<FeaturedCollectionUiModel>? = null,
        searchBarText: String? = null,
        hasHint: Boolean? = null,
        hasClearIcon: Boolean? = null,
        isLoading: Boolean? = null,
        noResultsFound: Boolean? = null,
        areMorePhotosIncoming: Boolean? = null
    ) {
        reduceState { state ->
            val consistentState = when (state) {
                is HomeScreenUiState.Failure -> {
                    state.copy(
                        curated = curated ?: state.curated,
                        featuredCollections = featuredCollections ?: state.featuredCollections,
                        searchBarText = searchBarText ?: state.searchBarText,
                        hasHint = hasHint ?: state.hasHint,
                        hasClearIcon = hasClearIcon ?: state.hasClearIcon,
                        isLoading = isLoading ?: state.isLoading
                    )
                }

                is HomeScreenUiState.Initial -> {
                    state.copy(
                        curated = curated ?: state.curated,
                        featuredCollections = featuredCollections ?: state.featuredCollections,
                        searchBarText = searchBarText ?: state.searchBarText,
                        hasHint = hasHint ?: state.hasHint,
                        hasClearIcon = hasClearIcon ?: state.hasClearIcon,
                        isLoading = isLoading ?: state.isLoading
                    )
                }

                is HomeScreenUiState.Success -> {
                    state.copy(
                        curated = curated ?: state.curated,
                        featuredCollections = featuredCollections ?: state.featuredCollections,
                        searchBarText = searchBarText ?: state.searchBarText,
                        hasHint = hasHint ?: state.hasHint,
                        hasClearIcon = hasClearIcon ?: state.hasClearIcon,
                        isLoading = isLoading ?: state.isLoading,
                        isLoadingOfMorePhotosInProcess = areMorePhotosIncoming
                            ?: state.isLoadingOfMorePhotosInProcess
                    )
                }
            }

            Log.d("BaseViewModel", T::class.toString())

            when (T::class) {
                HomeScreenUiState.Success::class -> {
                    HomeScreenUiState.Success(
                        curated = consistentState.curated,
                        noResultsFound = noResultsFound ?: false,
                        featuredCollections = consistentState.featuredCollections,
                        isLoading = consistentState.isLoading,
                        searchBarText = consistentState.searchBarText,
                        hasHint = consistentState.hasHint,
                        hasClearIcon = consistentState.hasClearIcon,
                        isLoadingOfMorePhotosInProcess = areMorePhotosIncoming ?: false
                    ) as T
                }

                HomeScreenUiState.Initial::class -> {
                    HomeScreenUiState.Initial(
                        curated = consistentState.curated,
                        featuredCollections = consistentState.featuredCollections,
                        isLoading = consistentState.isLoading,
                        searchBarText = consistentState.searchBarText,
                        hasHint = consistentState.hasHint,
                        hasClearIcon = consistentState.hasClearIcon
                    ) as T
                }

                HomeScreenUiState.Failure::class -> {
                    HomeScreenUiState.Failure(
                        curated = consistentState.curated,
                        featuredCollections = consistentState.featuredCollections,
                        isLoading = consistentState.isLoading,
                        searchBarText = consistentState.searchBarText,
                        hasHint = consistentState.hasHint,
                        hasClearIcon = consistentState.hasClearIcon
                    ) as T
                }

                else -> throw IllegalArgumentException("T is not a descendant of the HomeScreenUiState class")
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val TIME_INTERVAL_IN_MILLIS = 1000L
        private const val MIN_HEIGHT_OF_STAGGERED_ITEM_IN_DP = 150
        private const val MAX_HEIGHT_OF_STAGGERED_ITEM_IN_DP = 400
    }
}