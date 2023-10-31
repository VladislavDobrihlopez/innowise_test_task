package com.voitov.pexels_app.domain.repository

import com.voitov.pexels_app.domain.model.FeaturedCollection
import kotlinx.coroutines.flow.SharedFlow

interface PexelsFeaturedCollectionsRepository {
    fun getFeaturedCollections(): SharedFlow<List<FeaturedCollection>>
    suspend fun requestFeaturedCollections(page: Int, batch: Int)
}