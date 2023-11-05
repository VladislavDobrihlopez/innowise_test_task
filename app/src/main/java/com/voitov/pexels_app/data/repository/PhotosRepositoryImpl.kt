package com.voitov.pexels_app.data.repository

import android.util.Log
import com.voitov.pexels_app.data.database.dao.PhotosDao
import com.voitov.pexels_app.data.database.dao.UserPhotosDao
import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.cache.PersistentCacheManager
import com.voitov.pexels_app.data.datasource.cache.entity.PhotoDetailsCacheEntity
import com.voitov.pexels_app.data.datasource.remote.PhotoDownloaderRemoteSource
import com.voitov.pexels_app.data.datasource.remote.RemoteDataSource
import com.voitov.pexels_app.data.mapper.PhotosCacheMapper
import com.voitov.pexels_app.data.mapper.PhotosMapper
import com.voitov.pexels_app.data.network.dto.photo.PhotosHolder
import com.voitov.pexels_app.data.repository.model.PhotoRequestBatch
import com.voitov.pexels_app.di.annotation.DispatcherIO
import com.voitov.pexels_app.di.annotation.PhotosCache
import com.voitov.pexels_app.domain.OperationResult
import com.voitov.pexels_app.domain.PexelsException
import com.voitov.pexels_app.domain.model.Photo
import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(
    private val photosCacheDatabase: PhotosDao,
    private val bookmarkedPhotosDatabase: UserPhotosDao,
    @PhotosCache
    private val memoryCache: HotCacheDataSource<Int, PhotoDetailsCacheEntity, String>,
    private val photoDownloader: PhotoDownloaderRemoteSource,
    private val remotePhotosSource: RemoteDataSource,
    private val cacheMapper: PhotosCacheMapper,
    private val photosMapper: PhotosMapper,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
    private val cacheManager: PersistentCacheManager
) : PexelsPhotosRepository {
    private val pendingItemsForCaching = MutableSharedFlow<PhotoDetailsCacheEntity>(replay = 1)

    private val refreshPhotos =
        MutableSharedFlow<PhotoRequestBatch>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    init {
        observePendingItems()
    }

    private fun observePendingItems() {
        scope.launch {
            pendingItemsForCaching.collect {
                memoryCache.updateCache(it)
            }
        }
    }

    override fun getCuratedPhotos(): SharedFlow<OperationResult<List<Photo>, PexelsException>> =
        flow<OperationResult<List<Photo>, PexelsException>> {
            refreshPhotos.collect { batch ->

                cacheManager.cacheJob.join()

                try {
                    val response =
                        retrievePhotos(batch.query, batch.page, batch.pagesPerRequest)

                    if (response.isSuccessful) {
                        val photosEntities = response.body()?.photos?.map {
                            photosMapper.mapDtoToEntity(it, batch.query)
                        }?.filter {
                            !memoryCache.contains(it.id)
                        } ?: emptyList()

                        if (photosEntities.isNotEmpty()) {
                            photosCacheDatabase.addAll(photosEntities)
                            val cachesEntities =
                                photosEntities.map { cacheMapper.mapDbEntityToCacheEntity(it) }
                            memoryCache.updateCache(cachesEntities)
                        }
                    }
                } catch (ex: Exception) {
                    Log.d(TAG, ex.message.toString())
                    if (getCachedEntities(batch).isEmpty()) {
                        emit(OperationResult.Error(PexelsException.InternetConnectionFailedAndNoCache))
                    } else {
                        val domainModels =
                            getCachedEntities(batch)
                                .map { cacheMapper.mapCacheEntityToDomainModel(it) }

                        if (domainModels.isNotEmpty()) {
                            emit(
                                OperationResult.Error(PexelsException.NoInternet, domainModels)
                            )
                        }
                    }
                    return@collect
                }
                val domainModels =
                    getCachedEntities(batch).map { cacheMapper.mapCacheEntityToDomainModel(it) }

                emit(OperationResult.Success(domainModels))
            }
        }.shareIn(scope, SharingStarted.WhileSubscribed())

    private fun getCachedEntities(batch: PhotoRequestBatch) =
        memoryCache.getAllCache { it == batch.query }


    private var photosBeingRetrievedJob: Job? = null

    private suspend fun retrievePhotos(
        query: String,
        page: Int,
        inBatch: Int
    ): Response<PhotosHolder> {
        photosBeingRetrievedJob?.cancel()
        return coroutineScope {
            val job = async {
                if (query.isEmpty()) {
                    remotePhotosSource.getCuratedPhotos(page, inBatch)
                } else {
                    remotePhotosSource.searchForPhotos(query, page, inBatch)
                }
            }

            photosBeingRetrievedJob = job

            val response = job.await()

            response
        }
    }

    override suspend fun requestPhotos(query: String, page: Int, batch: Int) {
        refreshPhotos.emit(PhotoRequestBatch(query, page, batch))
    }

    override suspend fun getPhotoDetailsFromRemoteSource(photoId: Int): PhotoDetails {
        val tryFetchingFromCache: (Int) -> PhotoDetailsCacheEntity? = { id ->
            memoryCache.getItemById(id)
        }
        var photoDetailsCacheEntity = withContext(dispatcher) {
            try {
                val response = remotePhotosSource.getPhotoDetails(photoId)
                if (response.isSuccessful) {
                    val dto = response.body() ?: throw IllegalStateException()

                    val dbEntity =
                        photosCacheDatabase.getPhotoById(photoId).copy(author = dto.authorName)
                    photosCacheDatabase.upsertAll(listOf(dbEntity))

                    val toBeCachedEntity = cacheMapper.mapDbEntityToCacheEntity(dbEntity)
                    pendingItemsForCaching.emit(toBeCachedEntity)
                    toBeCachedEntity
                } else {
                    throw HttpException(response)
                }
            } catch (ex: Exception) {
                val cachedEntity = requireNotNull(tryFetchingFromCache(photoId))
                cacheMapper.mapCacheEntityToDomainModelDetails(cachedEntity)
                cachedEntity
            }
        }

        try {
            var cachedItem = requireNotNull(tryFetchingFromCache(photoId))
            cachedItem = if (bookmarkedPhotosDatabase.isItemExists(cachedItem.id)) {
                photoDetailsCacheEntity = photoDetailsCacheEntity.copy(isBookmarked = true)
                cachedItem.copy(isBookmarked = true)
            } else {
                photoDetailsCacheEntity = photoDetailsCacheEntity.copy(isBookmarked = false)
                cachedItem.copy(isBookmarked = false)
            }
            pendingItemsForCaching.emit(cachedItem)
        } catch (ex: Exception) {
            Log.d(TAG, ex.message.toString())
        }

        return cacheMapper.mapCacheEntityToDomainModelDetails(photoDetailsCacheEntity)
    }

    override suspend fun getPhotoDetailsFromLocalSource(photoId: Int): PhotoDetails {
        return withContext(dispatcher) {
            val dbEntities = photosCacheDatabase.getPhotoById(photoId)
            photosMapper.mapDbEntityToDomainModel(dbEntities)
        }
    }

    override suspend fun downloadPhoto(photoDetails: PhotoDetails): Result<Unit> {
        return withContext(dispatcher) {
            try {
                val isSuccess = photoDownloader.tryToDownloadPhoto(photoDetails)
                if (isSuccess) Result.success(Unit) else Result.failure(RuntimeException())
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }

    companion object {
        private const val TAG = "PexelsRepositoryImpl"
    }
}