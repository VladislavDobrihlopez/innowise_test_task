package com.voitov.pexels_app.domain.usecases

import com.voitov.pexels_app.domain.PexelsRepository
import javax.inject.Inject

class DownloadPhotoViaUrl @Inject constructor(
    private val repository: PexelsRepository
) {
    suspend operator fun invoke(networkUrl: String): Result<Unit> {
        return repository.downloadPhoto(networkUrl)
    }
}