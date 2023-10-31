package com.voitov.pexels_app.domain.usecase

import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import javax.inject.Inject

class DownloadPhotoViaUrl @Inject constructor(
    private val repository: PexelsPhotosRepository
) {
    suspend operator fun invoke(networkUrl: String): Result<Unit> {
        return repository.downloadPhoto(networkUrl)
    }
}