package com.voitov.pexels_app.data.repository

import com.voitov.pexels_app.data.datasource.LocalDataSource
import com.voitov.pexels_app.data.datasource.RemoteDataSource
import com.voitov.pexels_app.data.mapper.PexelsMapper
import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import retrofit2.HttpException
import javax.inject.Inject

class PexelsRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val mapper: PexelsMapper,
) : PexelsRepository {
    override suspend fun getFeaturedCollections(): Result<List<FeaturedCollection>> {
        return try {
            val response = remoteDataSource.getFeaturedCollections()
            val result = response.collections.map { mapper.mapDtoToDomainModel(it) }
            Result.success(result)
        } catch (ex: HttpException) {
            Result.failure(Throwable("fds"))
        }
    }

    override suspend fun getCuratedPhotos(): Result<List<Photo>> {
        return try {
            val response = remoteDataSource.getCuratedPhotos()
            val result = response.photos.map { mapper.mapDtoToDomainModel(it) }
            Result.success(result)
        } catch (ex: HttpException) {
            Result.failure(Throwable("fds"))
        }
    }
}