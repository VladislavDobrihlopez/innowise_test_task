package com.voitov.pexels_app.data.datasource

import com.voitov.pexels_app.data.network.dto.detailed_photo.PhotoDetailsDto
import com.voitov.pexels_app.data.network.dto.featured_collection.FeaturedCollectionsHolder
import com.voitov.pexels_app.data.network.dto.photo.PhotosHolder

interface RemoteDataSource {
    suspend fun getFeaturedCollections(): FeaturedCollectionsHolder
    suspend fun getCuratedPhotos(): PhotosHolder
    suspend fun searchForPhotos(query: String): PhotosHolder
    suspend fun getPhotoDetails(id: String): PhotoDetailsDto
}