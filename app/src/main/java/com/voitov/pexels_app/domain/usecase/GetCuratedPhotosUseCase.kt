package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import com.voitov.pexels_app.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCuratedPhotosUseCase @Inject constructor(
    private val repository: PexelsPhotosRepository
) {
    operator fun invoke(): Flow<List<Photo>> {
        return repository.getCuratedPhotos()
    }
}