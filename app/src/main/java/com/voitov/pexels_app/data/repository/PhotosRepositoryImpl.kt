package com.voitov.pexels_app.data.repository

import android.util.Log
import com.voitov.pexels_app.data.database.dao.PhotosDao
import com.voitov.pexels_app.data.database.dao.UserPhotosDao
import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.cache.entity.PhotoDetailsCacheEntity
import com.voitov.pexels_app.data.datasource.cache.implementation.PersistentCacheManagerImpl
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
    private val cacheManager: PersistentCacheManagerImpl
) : PexelsPhotosRepository {
    private val pendingItemsForCaching = MutableSharedFlow<PhotoDetailsCacheEntity>(replay = 1)

    private val refreshPhotos =
        MutableSharedFlow<PhotoRequestBatch>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    private var photosBeingRetrievedJob: Job? = null

    init {
        observePendingItems()
    }

    /**
     * Provides data about photos. these photos can be taken either from the in-memory cache
     * or from the api response.
     * Steps:
     * 1. Check whether the in-memory cache (HotCacheDataSource<Int, PhotoDetailsCacheEntity, String>)
     * has been initialized with data from the persistent cache (Pexels database, PhotoDetailsEntity).
     * Waits if needed
     * 2. Try to receive the data from the api and update caches.
     * if succeeded -> update persistent database and in-memory cache with new data
     * @return emit(OperationResult.Success(domainModels))
     * if failed -> receive data from in-memory cache if it is not empty and
     * @return emit(OperationResult.Error(PexelsException.NoInternet, domainModels)) otherwise
     * @return emit(OperationResult.Error(PexelsException.InternetConnectionFailedAndNoCache))
     * Note:
     * @see getCuratedPhotos() is a hot flow and works as long as there is at least one subscriber
     */
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
                        }?.filterNot {
                            memoryCache.contains(it.id)
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

                        emit(OperationResult.Error(PexelsException.NoInternet, domainModels))
                    }
                    return@collect
                }
                val domainModels =
                    getCachedEntities(batch).map { cacheMapper.mapCacheEntityToDomainModel(it) }

                emit(OperationResult.Success(domainModels))
            }
        }.shareIn(scope, SharingStarted.WhileSubscribed())

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

    private fun getCachedEntities(batch: PhotoRequestBatch) =
        memoryCache.getAllCache { it == batch.query }

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

    private fun observePendingItems() {
        scope.launch {
            pendingItemsForCaching.collect {
                memoryCache.updateCache(it)
            }
        }
    }

    companion object {
        private const val TAG = "PexelsRepositoryImpl"
    }
}