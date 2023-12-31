package com.voitov.pexels_app.presentation.home_screen

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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val uiMapper: UiMapper,
    curatedPhotosUseCase: GetCuratedPhotosUseCase,
    featuredCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val requestFeaturedCollectionsUseCase: RequestCollectionUseCase,
    private val requestPhotosUseCase: RequestNextPhotosUseCase,
) : BaseViewModel<HomeScreenSideEffect, HomeScreenUiState, HomeScreenEvent>(
    HomeScreenUiState.Initial()
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Resource(R.string.unexpected_error)))
        switchState<HomeScreenUiState.Failure>(isLoading = false, noResultsFound = false)
    }

    init {
        viewModelScope.launch(exceptionHandler) {
            loadScreenDataIfMissing()
        }
    }

    init {
        curatedPhotosUseCase()
            .onEach { operationResult ->
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
                                    uiMapper.mapDomainToUiModel(item)
                                }, isLoading = false)
                                sendSideEffect(
                                    HomeScreenSideEffect.ShowToast(
                                        UiText.Resource(R.string.no_internet_cached_data)
                                    )
                                )
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
                                    uiMapper.mapDomainToUiModel(item)
                                },
                                isLoading = false,
                                noResultsFound = false,
                                areMorePhotosIncoming = false,
                            )
                        }
                    }
                }
            }
            .catch {
                sendSideEffect(
                    HomeScreenSideEffect.ShowToast(
                        UiText.Resource(R.string.unexpected_error_curated)
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    init {
        featuredCollectionsUseCase()
            .onEach { collections ->
                updateCurrentState(featuredCollections = collections.map {
                    uiMapper.mapDomainToUiModel(it)
                }, isLoading = false)
            }
            .catch {
                sendSideEffect(HomeScreenSideEffect.ShowToast(UiText.Resource(R.string.unexpected_error_featured)))
            }
            .launchIn(viewModelScope)
    }

    private val _searchOnInternetEventContainer = MutableSharedFlow<String>()

    @OptIn(FlowPreview::class)
    private val search = flow<String> {
        _searchOnInternetEventContainer.debounce(SEARCH_DEBOUNCE_OF_SEARCH_BAR_IN_MILLIS).collect {
            emit(it)
        }
    }
        .onEach { query ->
            updateCurrentState(isLoading = true)
            val uiState = state.value
            if (uiState is HomeScreenUiState.Failure)
                requestPhotosUseCase(query, true)
            else
                requestPhotosUseCase(query)

            requestFeaturedCollectionsIfEmpty(uiState.featuredCollections)
        }
        .launchIn(viewModelScope)

    private var retrieveNewBunchJob: Job? = null

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
        if (retrieveNewBunchJob?.isActive == true) return
        retrieveNewBunchJob = viewModelScope.launch(exceptionHandler) {
            updateCurrentState(areMorePhotosIncoming = true)
            requestPhotosUseCase(searchBarText)
            delay(500) // to avoid requesting a few times at once
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
        viewModelScope.launch(exceptionHandler) {
            val updatedSearchBarText = item.title
            val updatedFeaturedCollections = getUpdatedFeaturedCollections { it == item }
            updateCurrentState(
                featuredCollections = updatedFeaturedCollections,
                searchBarText = updatedSearchBarText,
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
            viewModelScope.launch(exceptionHandler) {
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
        }
    }

    private fun handleOnExploreClick() {
        viewModelScope.launch(exceptionHandler) {
            require(_state.value.curated.isEmpty())
            updateCurrentState(
                searchBarText = "",
                hasClearIcon = false,
                isLoading = true
            )
            loadScreenDataIfMissing()
        }
    }

    private fun handleOnTryAgainClick() {
        viewModelScope.launch(exceptionHandler) {
            reduceState { state ->
                require(state is HomeScreenUiState.Failure)

                launch {
                    requestPhotosUseCase(state.searchBarText, keepPage = true)
                }
                if (state.featuredCollections.isEmpty()) {
                    launch {
                        requestFeaturedCollectionsUseCase()
                    }
                }

                state.copy(isLoading = true)
            }
        }
    }

    private suspend fun loadScreenDataIfMissing(searchBarText: String = "") {
        coroutineScope {
            with(_state.value) {
                if (curated.isEmpty()) {
                    launch {
                        requestPhotosUseCase(searchBarText)
                    }
                }
                launch {
                    requestFeaturedCollectionsIfEmpty(featuredCollections)
                }
            }
        }
    }

    private suspend fun requestFeaturedCollectionsIfEmpty(featuredCollections: List<FeaturedCollectionUiModel>) {
        if (featuredCollections.isEmpty()) {
            requestFeaturedCollectionsUseCase()
        }
    }

    private fun updateCurrentState(
        curated: List<CuratedUiModel>? = null,
        featuredCollections: List<FeaturedCollectionUiModel>? = null,
        searchBarText: String? = null,
        hasHint: Boolean? = null,
        hasClearIcon: Boolean? = null,
        isLoading: Boolean? = null,
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
        private const val SEARCH_DEBOUNCE_OF_SEARCH_BAR_IN_MILLIS = 1300L
    }
}