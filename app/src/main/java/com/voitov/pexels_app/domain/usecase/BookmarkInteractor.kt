package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.model.PhotoDetails
import javax.inject.Inject

class BookmarkInteractor @Inject constructor(
    private val addPhotoUseCase: BookmarkPhotoUseCase,
    private val deletePhotoUseCase: UnBookmarkPhotoUseCase
) {
    suspend operator fun invoke(photoId: Int, photoDetails: PhotoDetails, query: String) {
        if (photoDetails.isBookmarked) {
            deletePhotoUseCase(photoId)
        } else {
            addPhotoUseCase(photoDetails.copy(isBookmarked = true), query)
        }
    }
}