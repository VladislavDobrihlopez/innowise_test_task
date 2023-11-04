package com.voitov.pexels_app.presentation.bookmarks_screen

import androidx.lifecycle.viewModelScope
import com.voitov.pexels_app.domain.usecase.GetBookmarkedPhotosUseCase
import com.voitov.pexels_app.presentation.BaseViewModel
import com.voitov.pexels_app.presentation.bookmarks_screen.model.CuratedDetailedUiModel
import com.voitov.pexels_app.presentation.mapper.UiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    getAllPhotosUseCase: GetBookmarkedPhotosUseCase,
    private val mapper: UiMapper
) : BaseViewModel<BookmarksScreenSideEffect, BookmarksScreenUiState, BookmarksEvent>(
    BookmarksScreenUiState.Loading
) {

    private var items: List<CuratedDetailedUiModel> = emptyList()
    private var totalItems: Int = 0
        get() = items.size

    init {
        getAllPhotosUseCase()
            .onStart { delay(250) }
            .onEach {
                page = STARTING_PAGE
                items = it.map {
                    mapper.mapDomainToUiModel(it)
                }

                if (it.isNotEmpty()) {
                    handleOnLoadNewBunchOfPhotos()
                } else {
                    updateState(BookmarksScreenUiState.Failure)
                }
            }
            .catch {
                updateState(BookmarksScreenUiState.Failure)
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: BookmarksEvent) {
        when (event) {
            is BookmarksEvent.OnClickPhoto -> handleOnClickBookmarkedPhoto(event.photo)
            BookmarksEvent.OnClickExplore -> handleOnClickExplore()
            BookmarksEvent.OnLoadNewBunchOfPhotos -> handleOnLoadNewBunchOfPhotos()
        }
    }

    private fun handleOnClickBookmarkedPhoto(photo: CuratedDetailedUiModel) {
        sendSideEffect(BookmarksScreenSideEffect.NavigateToDetailsScreen(photo.id))
    }

    private fun handleOnClickExplore() {
        sendSideEffect(BookmarksScreenSideEffect.NavigateToHomeMainScreen)
    }

    private var page: Int = STARTING_PAGE

    private fun handleOnLoadNewBunchOfPhotos() {
        val newPhotos = items.take(BATCH_LIMIT * page)
        if (BATCH_LIMIT * page < totalItems) {
            page++
        }

        updateState(
            BookmarksScreenUiState.Success(
                photos = newPhotos,
                isPaginationInProgress = false
            )
        )
    }

    companion object {
        private const val STARTING_PAGE = 1
        private const val BATCH_LIMIT = 30
    }
}