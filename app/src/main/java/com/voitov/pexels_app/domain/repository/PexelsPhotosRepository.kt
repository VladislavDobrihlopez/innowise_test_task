package com.voitov.pexels_app.domain.repository

import com.voitov.pexels_app.domain.OperationResult
import com.voitov.pexels_app.domain.PexelsException
import com.voitov.pexels_app.domain.model.Photo
import com.voitov.pexels_app.domain.model.PhotoDetails
import kotlinx.coroutines.flow.SharedFlow

interface PexelsPhotosRepository {
    fun getCuratedPhotos(): SharedFlow<OperationResult<List<Photo>, PexelsException>>
    suspend fun requestPhotos(query: String, page: Int, batch: Int)
    suspend fun getPhotoDetailsFromRemoteSource(photoId: Int): PhotoDetails
    suspend fun getPhotoDetailsFromLocalSource(photoId: Int): PhotoDetails
    suspend fun downloadPhoto(photoDetails: PhotoDetails): Result<Unit>
}