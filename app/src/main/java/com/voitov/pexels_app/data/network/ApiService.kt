package com.voitov.pexels_app.data.network

import com.voitov.pexels_app.data.network.dto.detailed_photo.PhotoDetailsDto
import com.voitov.pexels_app.data.network.dto.featured_collection.FeaturedCollectionsHolder
import com.voitov.pexels_app.data.network.dto.photo.PhotosHolder
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("v1/collections/featured")
    suspend fun getFeaturedCollections(
        @Query("page") page: Int,
        @Query("per_page") count: Int
    ): Response<FeaturedCollectionsHolder>

    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Query("page") page: Int,
        @Query("per_page") count: Int
    ): Response<PhotosHolder>

    @GET("v1/search")
    suspend fun searchForPhotos(
        @Query("page") page: Int,
        @Query("per_page") count: Int,
        @Query("query") query: String
    ): Response<PhotosHolder>

    @GET("v1/photos/{id}")
    suspend fun getPhotoDetails(
        @Path("id") photoId: Int
    ): Response<PhotoDetailsDto>

    companion object {
        const val BASE_URL = "https://api.pexels.com/"
        const val CUSTOM_AUTH_HEADER = "Authorization"
        const val TOKEN = "E0Yymn8ICVbKaroPC0JOarSlaK09gXT177Ykixqfa05prWL4PBB1Rc4D"
    }
}