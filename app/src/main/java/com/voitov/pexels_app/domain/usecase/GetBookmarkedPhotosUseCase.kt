package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.domain.repository.PexelsBookmarkedPhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedPhotosUseCase @Inject constructor(
    private val repository: PexelsBookmarkedPhotosRepository,
) {
    operator fun invoke(): Flow<List<PhotoDetails>> {
        return repository.getAllBookmarkedPhotos()
    }
}