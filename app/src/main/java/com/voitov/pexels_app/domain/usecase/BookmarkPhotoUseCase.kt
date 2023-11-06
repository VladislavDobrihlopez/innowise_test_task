package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.domain.repository.PexelsBookmarkedPhotosRepository
import javax.inject.Inject

class BookmarkPhotoUseCase @Inject constructor(
    private val repository: PexelsBookmarkedPhotosRepository
) {
    suspend operator fun invoke(item: PhotoDetails, query: String) {
        repository.insertPhoto(item, query)
    }
}