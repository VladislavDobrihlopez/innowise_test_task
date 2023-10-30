package com.voitov.pexels_app.data.repository

import android.util.Log
import com.voitov.pexels_app.data.datasource.LocalDataSource
import com.voitov.pexels_app.data.datasource.RemoteDataSource
import com.voitov.pexels_app.data.mapper.PexelsMapper
import com.voitov.pexels_app.data.network.dto.photo.PhotoDto
import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.domain.models.PhotoDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PexelsRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val mapper: PexelsMapper,
) : PexelsRepository {
    private val dispatcher = Dispatchers.IO
    private val scope = CoroutineScope(dispatcher)

    private val refreshCollection = MutableSharedFlow<Unit>(replay = 1)

    override fun getFeaturedCollections() = flow<List<FeaturedCollection>> {
        refreshCollection.collect {
            try {
                val response = withContext(dispatcher) {
                    remoteDataSource.getFeaturedCollections()
                }
                val result = response.collections.map { mapper.mapDtoToDomainModel(it) }
                emit(result)
            } catch (ex: Exception) {
                Log.d(TAG, ex::class.toString())
                emit(emptyList())
            }
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(5000))

    private val refreshPhotos =
        MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getCuratedPhotos() = flow<List<Photo>> {
        refreshPhotos.collect { query ->
            val photosDto = retrievePhotos(query)
            val mappedPhotos = photosDto.map { mapper.mapDtoToDomainModel(it) }
            emit(mappedPhotos)
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(5000))

    private var photosBeingRetrievedJob: Job? = null

    private suspend fun retrievePhotos(query: String): List<PhotoDto> {
        try {
            Log.d(TAG, "requestPhotos: in")
            photosBeingRetrievedJob?.cancel()

            val photosBeingRetrievedJob = withContext(dispatcher) {
                if (query.isEmpty()) {
                    async {
                        remoteDataSource.getCuratedPhotos()
                    }
                } else {
                    async {
                        remoteDataSource.searchForPhotos(query)
                    }
                }
            }

            val response = photosBeingRetrievedJob.await()
            Log.d(TAG, "requestPhotos out: ${response.photos}")
            return response.photos
        } catch (ex: Exception) {
            Log.d(TAG, ex::class.toString())
            return emptyList()
        }
    }

    override suspend fun requestPhotos(query: String) {
        refreshPhotos.emit(query)
    }

    override suspend fun requestFeaturedCollections() {
        refreshCollection.emit(Unit)
    }


    override suspend fun getPhotoDetailsFromRemoteSource(photoId: Int): PhotoDetails {
        return withContext(dispatcher) {
            val dtos = remoteDataSource.getPhotoDetails(photoId)
            mapper.mapDtoToDomainModel(dtos)
        }
    }

    override suspend fun getPhotoDetailsFromLocalSource(photoId: Int): PhotoDetails {
        return withContext(dispatcher) {
            val dbEntities = localDataSource.getImage(photoId)
            mapper.mapDbEntityToDomainModel(dbEntities)
        }
    }

    override suspend fun downloadPhoto(url: String): Result<Unit> {
        return withContext(dispatcher) {
            try {
                remoteDataSource.downloadPhoto(url) { success ->
                    if (success)
                        Result.success(Unit)
                }
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }

    companion object {
        private const val TAG = "PexelsRepositoryImpl"
    }
}