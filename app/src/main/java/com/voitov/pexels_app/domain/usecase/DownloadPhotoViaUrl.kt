package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import javax.inject.Inject

class DownloadPhotoViaUrl @Inject constructor(
    private val repository: PexelsPhotosRepository
) {
    suspend operator fun invoke(photoDetails: PhotoDetails): Result<Unit> {
        return repository.downloadPhoto(photoDetails)
    }
}