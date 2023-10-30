package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.AppMainSections
import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.PhotoDetails
import javax.inject.Inject

class GetPhotoDetailsUseCase @Inject constructor(
    private val repository: PexelsRepository
) {
    suspend operator fun invoke(observer: AppMainSections, photoId: Int): PhotoDetails {
        return when (observer) {
            AppMainSections.HOME_SCREEN -> repository.getPhotoDetailsFromRemoteSource(photoId)
            AppMainSections.BOOKMARKS_SCREEN -> repository.getPhotoDetailsFromLocalSource(photoId)
        }
    }
}