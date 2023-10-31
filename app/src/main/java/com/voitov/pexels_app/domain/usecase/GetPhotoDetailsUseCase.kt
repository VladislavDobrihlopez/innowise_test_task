package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.AppMainSections
import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import com.voitov.pexels_app.domain.model.PhotoDetails
import javax.inject.Inject

class GetPhotoDetailsUseCase @Inject constructor(
    private val repository: PexelsPhotosRepository
) {
    suspend operator fun invoke(observer: AppMainSections, photoId: Int): PhotoDetails {
        return when (observer) {
            AppMainSections.HOME_SCREEN -> repository.getPhotoDetailsFromRemoteSource(photoId)
            AppMainSections.BOOKMARKS_SCREEN -> repository.getPhotoDetailsFromLocalSource(photoId)
        }
    }
}