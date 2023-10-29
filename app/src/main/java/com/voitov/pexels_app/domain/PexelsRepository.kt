package com.voitov.pexels_app.domain

import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface PexelsRepository {
    fun getFeaturedCollections(): SharedFlow<List<FeaturedCollection>>
    fun getCuratedPhotos(): SharedFlow<List<Photo>>
    suspend fun requestPhotos(query: String)
    suspend fun requestFeaturedCollections()
}