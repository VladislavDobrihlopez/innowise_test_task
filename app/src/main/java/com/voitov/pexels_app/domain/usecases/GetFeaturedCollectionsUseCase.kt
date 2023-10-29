package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.FeaturedCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFeaturedCollectionsUseCase @Inject constructor(
    private val repository: PexelsRepository
) {
    operator fun invoke(): Flow<List<FeaturedCollection>> {
        return repository.getFeaturedCollections().map { it.take(7) }
    }
}


