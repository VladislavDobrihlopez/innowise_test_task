package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.FeaturedCollection
import javax.inject.Inject

class GetFeaturedCollectionsUseCase @Inject constructor(
    private val repository: PexelsRepository
) {
    suspend operator fun invoke(): Result<List<FeaturedCollection>> {
        return repository.getFeaturedCollections().map { it.take(7) }
    }
}
