package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.repository.PexelsBookmarkedPhotosRepository
import javax.inject.Inject

class UnBookmarkPhotoUseCase @Inject constructor(
    private val repository: PexelsBookmarkedPhotosRepository
) {
    suspend operator fun invoke(photoId: Int) {
        repository.deletePhotoById(photoId)
    }
}