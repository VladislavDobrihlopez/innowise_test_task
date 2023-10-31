package com.voitov.pexels_app.data.datasource.remote

import com.voitov.pexels_app.data.network.dto.detailed_photo.PhotoDetailsDto
import com.voitov.pexels_app.data.network.dto.featured_collection.FeaturedCollectionsHolder
import com.voitov.pexels_app.data.network.dto.photo.PhotosHolder
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getFeaturedCollections(page: Int, batch: Int): Response<FeaturedCollectionsHolder>
    suspend fun getCuratedPhotos(page: Int, batch: Int): Response<PhotosHolder>
    suspend fun searchForPhotos(query: String, page: Int, batch: Int): Response<PhotosHolder>
    suspend fun getPhotoDetails(id: Int): Response<PhotoDetailsDto>
    fun downloadPhoto(url: String, onResult: (Boolean) -> Unit)
}