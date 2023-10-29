package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.Photo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCuratedPhotosUseCase @Inject constructor(
    private val repository: PexelsRepository
) {
    operator fun invoke(): Flow<List<Photo>> {
        return repository.getCuratedPhotos()
    }
}