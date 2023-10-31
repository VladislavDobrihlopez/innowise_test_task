package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.model.PhotoDetails
import javax.inject.Inject

class BookmarkInteractor @Inject constructor(
    private val addUseCase: BookmarkPhotoUseCase,
    private val deleteUseCase: UnBookmarkPhotoUseCase
) {
    suspend operator fun invoke(photoId: Int, photoDetails: PhotoDetails, query: String) {
        if (photoDetails.isBookmarked) {
            deleteUseCase(photoId)
        } else {
            addUseCase(photoDetails.copy(isBookmarked = true), query)
        }
    }
}