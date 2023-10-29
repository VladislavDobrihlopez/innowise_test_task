package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.PexelsRepository
import javax.inject.Inject

class RequestCollectionUseCase @Inject constructor(
    private val repository: PexelsRepository
) {
    suspend operator fun invoke() {
        repository.requestFeaturedCollections()
    }
}