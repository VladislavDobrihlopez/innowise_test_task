package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.PexelsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestNextPhotosUseCase @Inject constructor(
    private val repository: PexelsRepository
) {
    private var page: Int = STARTING_PAGE
    private var previousQuery = "#+-"

    suspend operator fun invoke(query: String = SHOULD_SEARCH_FOR_CURATED) {
        if (previousQuery == query) {
            page++
        } else {
            page = STARTING_PAGE
            previousQuery = query
        }
        repository.requestPhotos(query, page, BATCH_LIMIT)
    }

    companion object {
        const val SHOULD_SEARCH_FOR_CURATED = ""
        const val STARTING_PAGE = 1
        const val BATCH_LIMIT = 30
    }
}