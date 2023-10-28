package com.voitov.pexels_app.domain

import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import kotlinx.coroutines.flow.Flow

interface PexelsRepository {
    suspend fun getFeaturedCollections(): Result<List<FeaturedCollection>>
    suspend fun getCuratedPhotos(): Result<List<Photo>>
}