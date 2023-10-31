package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.model.FeaturedCollection
import com.voitov.pexels_app.domain.repository.PexelsFeaturedCollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFeaturedCollectionsUseCase @Inject constructor(
    private val repository: PexelsFeaturedCollectionsRepository
) {
    operator fun invoke(): Flow<List<FeaturedCollection>> {
        return repository.getFeaturedCollections().map { it.take(FEATURED_COLLECTIONS_NUMBER) }
    }

    companion object {
        const val FEATURED_COLLECTIONS_NUMBER = 7
    }
}


