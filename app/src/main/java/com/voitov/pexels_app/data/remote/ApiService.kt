package com.voitov.pexels_app.data.remote

import com.voitov.pexels_app.data.remote.dto.detailed_photo.PhotoDetailsDto
import com.voitov.pexels_app.data.remote.dto.featured_collection.FeaturedCollectionsHolder
import com.voitov.pexels_app.data.remote.dto.photo.PhotosHolder
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("v1/collections/featured")
    suspend fun getFeaturedCollections(
        @Query("page") page: Int,
        @Query("per_page") count: Int
    ): FeaturedCollectionsHolder

    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Query("page") page: Int,
        @Query("per_page") count: Int
    ): PhotosHolder

    @GET("v1/search")
    suspend fun searchForPhotos(
        @Query("page") page: Int,
        @Query("per_page") count: Int,
        @Query("query") query: String
    ): PhotosHolder

    @GET("v1/photos/{id}")
    suspend fun getPhotoDetails(
        @Path("id") photoId: Int
    ): PhotoDetailsDto
}