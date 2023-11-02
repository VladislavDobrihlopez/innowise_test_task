package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestNextPhotosUseCase @Inject constructor(
    private val repository: PexelsPhotosRepository
) {
    private var page: Int = STARTING_PAGE
    private var previousQuery: String? = null

    suspend operator fun invoke(
        query: String = SHOULD_SEARCH_FOR_CURATED,
        keepPage: Boolean = false,
    ) {
        if (keepPage && previousQuery == query) {
            repository.requestPhotos(query, page, BATCH_LIMIT)
            return
        }

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