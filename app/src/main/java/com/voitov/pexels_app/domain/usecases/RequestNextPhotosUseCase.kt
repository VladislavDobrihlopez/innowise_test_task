package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.PexelsRepository
import javax.inject.Inject

class RequestNextPhotosUseCase @Inject constructor(
    private val repository: PexelsRepository
) {
    suspend operator fun invoke(query: String = SHOULD_SEARCH_FOR_CURATED) {
        repository.requestPhotos(query)
    }

    companion object {
        const val SHOULD_SEARCH_FOR_CURATED = ""
    }
}