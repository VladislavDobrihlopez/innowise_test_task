package com.voitov.pexels_app.domain.repository

import com.voitov.pexels_app.domain.model.PhotoDetails
import kotlinx.coroutines.flow.Flow

interface PexelsBookmarkedPhotosRepository {
    fun getAllBookmarkedPhotos(): Flow<List<PhotoDetails>>
    suspend fun insertPhoto(item: PhotoDetails, query: String)
    suspend fun deletePhotoById(id: Int)
}