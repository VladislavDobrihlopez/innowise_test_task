package com.voitov.pexels_app.data.repository

import android.util.Log
import com.voitov.pexels_app.data.database.dao.PhotosDao
import com.voitov.pexels_app.data.database.dao.UserPhotosDao
import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.cache.PhotosCacheMapper
import com.voitov.pexels_app.data.datasource.cache.entity.PhotoDetailsCacheEntity
import com.voitov.pexels_app.data.datasource.local.LocalDataSource
import com.voitov.pexels_app.data.datasource.remote.RemoteDataSource
import com.voitov.pexels_app.data.mapper.PhotosMapper
import com.voitov.pexels_app.data.network.dto.photo.PhotosHolder
import com.voitov.pexels_app.data.repository.helper.PhotoRequestBatch
import com.voitov.pexels_app.di.annotation.DispatcherIO
import com.voitov.pexels_app.di.annotation.PhotosCache
import com.voitov.pexels_app.domain.model.Photo
import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import com.voitov.pexels_app.domain.usecase.RequestNextPhotosUseCase.Companion.STARTING_PAGE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val photosDao: PhotosDao,
    private val bookmarkedPhotosDao: UserPhotosDao,
    @PhotosCache
    private val inMemoryHotCache: HotCacheDataSource<Int, PhotoDetailsCacheEntity, String>,
    private val remoteDataSource: RemoteDataSource,
    private val cacheMapper: PhotosCacheMapper,
    private val mapper: PhotosMapper,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope
) : PexelsPhotosRepository {
    private val pendingItemsForCaching = MutableSharedFlow<PhotoDetailsCacheEntity>(replay = 1)

    private val refreshPhotos =
        MutableSharedFlow<PhotoRequestBatch>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    init {
        initCache()
        observePendingItems()
    }

    private fun initCache() {
        scope.launch {
            val cachedItems = photosDao.getAllPhotos()
            inMemoryHotCache.updateCache(cachedItems.map {
                cacheMapper.mapDbEntityToCacheEntity(it)
            })
        }
    }

    private fun observePendingItems() {
        scope.launch {
            pendingItemsForCaching.collect {
                inMemoryHotCache.updateCache(it)
            }
        }
    }

    override fun getCuratedPhotos() = flow<List<Photo>> {
        refreshPhotos.collect { batch ->
            try {
                val response =
                    retrievePhotos(batch.query, batch.page, batch.pagesPerRequest)

                if (response.isSuccessful) {
                    val photosEntities = response.body()?.photos?.map {
                        mapper.mapDtoToEntity(it, batch.query)
                    }?.filter {
                        !inMemoryHotCache.contains(it.id)
                    } ?: emptyList()

                    if (photosEntities.isNotEmpty()) {
                        photosDao.addAll(photosEntities)
                        val cachesEntities =
                            photosEntities.map { cacheMapper.mapDbEntityToCacheEntity(it) }
                        inMemoryHotCache.updateCache(cachesEntities)
                    }
                }
            } catch (ex: Exception) {
                Log.d(TAG, ex.message.toString())
            }
            val mappedPhotos = inMemoryHotCache
                .getAllCache { it == batch.query }
                .map { cacheMapper.mapCacheEntityToDomainModel(it) }
            Log.d(TAG, "getCuratedPhotos: $mappedPhotos")
//            val mappedPhotos = photosDto.map { mapper.mapDtoToDomainModel(it) }
            emit(mappedPhotos)
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(5000))

    private var photosBeingRetrievedJob: Job? = null

    private suspend fun retrievePhotos(
        query: String,
        page: Int,
        inBatch: Int
    ): Response<PhotosHolder> {
        Log.d(TAG, "requestPhotos: in $page $inBatch")
        photosBeingRetrievedJob?.cancel()
        return coroutineScope {
            val photosBeingRetrievedJob =
                if (query.isEmpty()) {
                    async {
                        remoteDataSource.getCuratedPhotos(page, inBatch)
                    }
                } else {
                    async {
                        remoteDataSource.searchForPhotos(query, page, inBatch)
                    }
                }

            val response = photosBeingRetrievedJob.await()
            val data = response.body()?.photos
            Log.d(TAG, "requestPhotos out: $data")

            response
        }
    }

    override suspend fun requestPhotos(query: String, page: Int, batch: Int) {
        if (page == STARTING_PAGE) {
            //inMemoryHotCache.clear()
        }
        refreshPhotos.emit(PhotoRequestBatch(query, page, batch))
    }

    override suspend fun getPhotoDetailsFromRemoteSource(photoId: Int): PhotoDetails {
        val tryFetchingFromCache: (Int) -> PhotoDetailsCacheEntity? = { id ->
            inMemoryHotCache.getItemById(id)
        }
        var photoDetailsCacheEntity = withContext(dispatcher) {
            try {
                val response = remoteDataSource.getPhotoDetails(photoId)
                if (response.isSuccessful) {
                    val dto = response.body() ?: throw IllegalStateException()

                    val dbEntity = photosDao.getPhotoById(photoId).copy(author = dto.authorName)
                    photosDao.upsertAll(listOf(dbEntity))

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
            cachedItem = if (bookmarkedPhotosDao.isItemExists(cachedItem.id)) {
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
            val dbEntities = photosDao.getPhotoById(photoId)
            mapper.mapDbEntityToDomainModel(dbEntities)
        }
    }

    override suspend fun downloadPhoto(photoDetails: PhotoDetails): Result<Unit> {
        return withContext(dispatcher) {
            try {
                val isSuccess = localDataSource.tryToDownloadPhoto(photoDetails)
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