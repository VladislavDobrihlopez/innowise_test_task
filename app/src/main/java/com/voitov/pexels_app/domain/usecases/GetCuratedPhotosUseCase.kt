package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.Photo
import javax.inject.Inject

class GetCuratedPhotosUseCase @Inject constructor(
    private val repository: PexelsRepository
) {
    suspend operator fun invoke(): Result<List<Photo>> {
        return repository.getCuratedPhotos()
    }
}