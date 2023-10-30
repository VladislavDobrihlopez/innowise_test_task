package com.voitov.pexels_app.data.repository

import android.util.Log
import com.voitov.pexels_app.data.database.PhotoDetailsEntity
import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.local.LocalDataSource
import com.voitov.pexels_app.data.datasource.remote.RemoteDataSource
import com.voitov.pexels_app.data.mapper.PexelsMapper
import com.voitov.pexels_app.data.network.dto.photo.PhotoDto
import com.voitov.pexels_app.data.network.dto.photo.PhotosHolder
import com.voitov.pexels_app.di.CacheDataSourceQualifier
import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.domain.models.PhotoDetails
import com.voitov.pexels_app.domain.usecases.RequestNextPhotosUseCase.Companion.STARTING_PAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PexelsRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    @CacheDataSourceQualifier
    private val cacheDataSource: HotCacheDataSource<Int, PhotoDetailsEntity>,
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
        MutableSharedFlow<RequestBatch>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getCuratedPhotos() = flow<List<Photo>> {
        refreshPhotos.collect { batch ->
            val response = retrievePhotos(batch.query, batch.page, batch.pagesInBatch)
            val result = if (response.isSuccessful) {
                val photosEntities = response.body()?.photos?.map {
                    mapper.mapDtoToEntity(it)
                }?.filter {
                    !cacheDataSource.contains(it.id)
                } ?: emptyList()

                photosEntities?.let {
//                    database.addCats(catsEntities)
                    cacheDataSource.updateCache(it)
                }
            } else {
                throw IllegalStateException()
            }
            val mappedPhotos = cacheDataSource.getValue().map { Photo(id = it.id, url = it.networkUrl) }
            Log.d(TAG, "getCuratedPhotos: $mappedPhotos")
//            val mappedPhotos = photosDto.map { mapper.mapDtoToDomainModel(it) }
            emit(mappedPhotos)
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(5000))

    private var photosBeingRetrievedJob: Job? = null

    private suspend fun retrievePhotos(query: String, page: Int, inBatch: Int): Response<PhotosHolder> {
            Log.d(TAG, "requestPhotos: in $page $inBatch")
            photosBeingRetrievedJob?.cancel()

            val photosBeingRetrievedJob = withContext(dispatcher) {
                if (query.isEmpty()) {
                    async {
                        remoteDataSource.getCuratedPhotos(page, inBatch)
                    }
                } else {
                    async {
                        remoteDataSource.searchForPhotos(query, page, inBatch)
                    }
                }
            }

            val response = photosBeingRetrievedJob.await()
            val data = response.body()?.photos
            Log.d(TAG, "requestPhotos out: $data")

            return response
    }

    override suspend fun requestPhotos(query: String, page: Int, batch: Int) {
        if (page == STARTING_PAGE) {
            cacheDataSource.clear()
        }
        refreshPhotos.emit(RequestBatch(query, page, batch))
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