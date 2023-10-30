package com.voitov.pexels_app.domain

import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.domain.models.PhotoDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface PexelsRepository {
    fun getFeaturedCollections(): SharedFlow<List<FeaturedCollection>>
    fun getCuratedPhotos(): SharedFlow<List<Photo>>
    suspend fun requestPhotos(query: String)
    suspend fun requestFeaturedCollections()

    suspend fun getPhotoDetailsFromRemoteSource(photoId: Int): PhotoDetails
    suspend fun getPhotoDetailsFromLocalSource(photoId: Int): PhotoDetails
    suspend fun downloadPhoto(url: String): Result<Unit>
}