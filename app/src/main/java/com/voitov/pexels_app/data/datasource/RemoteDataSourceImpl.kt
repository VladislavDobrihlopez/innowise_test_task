package com.voitov.pexels_app.data.datasource

import com.voitov.pexels_app.data.network.ApiService
import com.voitov.pexels_app.data.network.dto.detailed_photo.PhotoDetailsDto
import com.voitov.pexels_app.data.network.dto.featured_collection.FeaturedCollectionsHolder
import com.voitov.pexels_app.data.network.dto.photo.PhotosHolder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService,
) : RemoteDataSource {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getFeaturedCollections(): FeaturedCollectionsHolder {
        return withContext(dispatcher) {
            apiService.getFeaturedCollections(1, 30)
        }
    }

    override suspend fun getCuratedPhotos(): PhotosHolder {
        return withContext(dispatcher) {
            apiService.getCuratedPhotos(1, 30)
        }
    }

    override suspend fun searchForPhotos(query: String): PhotosHolder {
        return withContext(dispatcher) {
            TODO()
        }
    }

    override suspend fun getPhotoDetails(id: String): PhotoDetailsDto {
        return withContext(dispatcher) {
            TODO()
        }
    }
}