package com.voitov.pexels_app.presentation.home_screen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.voitov.pexels_app.R
import com.voitov.pexels_app.domain.OperationResult
import com.voitov.pexels_app.domain.PexelsException
import com.voitov.pexels_app.domain.usecase.GetCuratedPhotosUseCase
import com.voitov.pexels_app.domain.usecase.GetFeaturedCollectionsUseCase
import com.voitov.pexels_app.domain.usecase.RequestCollectionUseCase
import com.voitov.pexels_app.domain.usecase.RequestNextPhotosUseCase
import com.voitov.pexels_app.presentation.BaseViewModel
import com.voitov.pexels_app.presentation.home_screen.model.CuratedUiModel
import com.voitov.pexels_app.presentation.home_screen.model.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.mapper.UiMapper
import com.voitov.pexels_app.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    curatedPhotosUseCase: GetCuratedPhotosUseCase,
    featuredCollectionsUseCase: GetFeaturedCollectionsUseCase,
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

    init {
        curatedPhotosUseCase()
            .onEach { operationResult ->
                Log.d(TAG, "curatedPhotosUseCase: $operationResult")

                when (operationResult) {
                    is OperationResult.Error -> {
                        when (operationResult.throwable) {
                            PexelsException.InternetConnectionFailedAndNoCache -> {
                                switchState<HomeScreenUiState.Failure>()
                            }

                            PexelsException.NoInternet -> {
                                val data = operationResult.data
                                requireNotNull(data)
                                switchState<HomeScreenUiState.Success>(curated = data.map { item ->
                                    mapper.mapDomainToUiModel(item)
                                }, isLoading = false)
                                sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Runtime("No internet, cached data")))
                            }
                        }
                    }

                    is OperationResult.Success -> {
                        val photos = operationResult.data
                        if (photos.isEmpty()) {
                            switchState<HomeScreenUiState.Success>(
                                curated = emptyList(),
                                isLoading = false,
                                noResultsFound = true
                            )
                        } else {
                            switchState<HomeScreenUiState.Success>(
                                curated = photos.map { item ->
                                    mapper.mapDomainToUiModel(item)
                                },
                                isLoading = false,
                                noResultsFound = false,
                                areMorePhotosIncoming = false
                            )
                        }
                    }
                }
            }
            .catch { ex ->
                sendSideEffect(
                    HomeScreenSideEffect.ShowToast(
                        UiText.Resource(R.string.unexpected_error_curated)
                    )
                )
            }
            .onCompletion {
                Log.d(TAG, "completed")
            }
            .launchIn(viewModelScope)
    }

    init {
        featuredCollectionsUseCase()
            .onEach {
                Log.d(TAG, "featuredCollectionsUseCase: $it")
                updateCurrentState(featuredCollections = it.map {
                    mapper.mapDomainToUiModel(it)
                }, isLoading = false)
            }
            .catch { ex ->
                sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Resource(R.string.unexpected_error_featured)))
            }
            .launchIn(viewModelScope)
    }

    private val _searchOnInternetEventContainer = MutableSharedFlow<String>()

    @OptIn(FlowPreview::class)
    private val search = flow<String> {
        _searchOnInternetEventContainer.debounce(TIME_INTERVAL_IN_MILLIS).collect {
            emit(it)
        }
    }
        .onEach { query ->
            updateCurrentState(isLoading = true)

            if (state.value is HomeScreenUiState.Failure)
                requestPhotosUseCase(query, true)
            else
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

    private var retrieveNewBunchJob: Job? = null

    private fun handleOnLoadNewBunchOfPhotos(searchBarText: String) {
        if (retrieveNewBunchJob?.isActive == true) return
        retrieveNewBunchJob = viewModelScope.launch {
            updateCurrentState(areMorePhotosIncoming = true)
            requestPhotosUseCase(searchBarText)
            delay(1500)
        }
    }

    private fun handleOnClickCurated(item: CuratedUiModel) {
        sendSideEffect(
            HomeScreenSideEffect.NavigateToDetailsScreen(
                item.id,
                _state.value.searchBarText
            )
        )
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

            if (state.value is HomeScreenUiState.Failure)
                requestPhotosUseCase(searchText, keepPage = true)
            else
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
                require(state is HomeScreenUiState.Failure)

                launch {
                    requestPhotosUseCase(state.searchBarText, keepPage = true)
                }
                launch {
                    if (state.featuredCollections.isEmpty()) {
                        requestFeaturedCollectionsUseCase()
                    }
                }

                state.copy(isLoading = true)
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
    }
}