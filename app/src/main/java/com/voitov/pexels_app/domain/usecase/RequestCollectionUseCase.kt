package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.repository.PexelsFeaturedCollectionsRepository
import javax.inject.Inject

class RequestCollectionUseCase @Inject constructor(
    private val repository: PexelsFeaturedCollectionsRepository
) {
    private val page = 1
    suspend operator fun invoke() {
        repository.requestFeaturedCollections(page = page, batch = BATCH_LIMIT)
    }

    companion object {
        const val BATCH_LIMIT = 30
    }
}