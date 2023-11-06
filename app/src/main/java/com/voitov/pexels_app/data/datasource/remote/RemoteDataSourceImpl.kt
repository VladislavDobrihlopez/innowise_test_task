package com.voitov.pexels_app.data.datasource.remote

import com.voitov.pexels_app.data.network.ApiService
import com.voitov.pexels_app.data.network.dto.detailed_photo.PhotoDetailsDto
import com.voitov.pexels_app.data.network.dto.featured_collection.FeaturedCollectionsHolder
import com.voitov.pexels_app.data.network.dto.photo.PhotosHolder
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService,
) : RemoteDataSource {
    override suspend fun getFeaturedCollections(
        page: Int,
        batch: Int
    ): Response<FeaturedCollectionsHolder> {
        return apiService.getFeaturedCollections(page, batch)
    }

    override suspend fun getCuratedPhotos(page: Int, batch: Int): Response<PhotosHolder> {
        return apiService.getCuratedPhotos(page = page, count = batch)
    }

    override suspend fun searchForPhotos(
        query: String,
        page: Int,
        batch: Int
    ): Response<PhotosHolder> {
        return apiService.searchForPhotos(page = page, count = batch, query)
    }

    override suspend fun getPhotoDetails(id: Int): Response<PhotoDetailsDto> {
        return apiService.getPhotoDetails(id)
    }
}