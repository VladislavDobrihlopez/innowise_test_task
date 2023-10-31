package com.voitov.pexels_app.domain.repository

import com.voitov.pexels_app.domain.model.Photo
import com.voitov.pexels_app.domain.model.PhotoDetails
import kotlinx.coroutines.flow.SharedFlow

interface PexelsPhotosRepository {
    fun getCuratedPhotos(): SharedFlow<List<Photo>>
    suspend fun requestPhotos(query: String, page: Int, batch: Int)
    suspend fun getPhotoDetailsFromRemoteSource(photoId: Int): PhotoDetails
    suspend fun getPhotoDetailsFromLocalSource(photoId: Int): PhotoDetails
    suspend fun downloadPhoto(url: String): Result<Unit>
}