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
        var formattedQuery = query
        if (query.isNotEmpty()) {
            formattedQuery = formattedQuery.replaceFirstChar { it.uppercase() }
        }
        if (keepPage && previousQuery == formattedQuery) {
            repository.requestPhotos(formattedQuery, page, BATCH_LIMIT)
            return
        }

        if (previousQuery == formattedQuery) {
            page++
        } else {
            page = STARTING_PAGE
            previousQuery = formattedQuery
        }
        repository.requestPhotos(formattedQuery, page, BATCH_LIMIT)
    }

    companion object {
        const val SHOULD_SEARCH_FOR_CURATED = ""
        const val STARTING_PAGE = 1
        const val BATCH_LIMIT = 30
    }
}