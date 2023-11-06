package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.OperationResult
import com.voitov.pexels_app.domain.PexelsException
import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import com.voitov.pexels_app.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCuratedPhotosUseCase @Inject constructor(
    private val repository: PexelsPhotosRepository
) {
    operator fun invoke(): Flow<OperationResult<List<Photo>, PexelsException>> {
        return repository.getCuratedPhotos()
    }
}