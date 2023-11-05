package com.voitov.pexels_app.data.datasource.cache

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.voitov.pexels_app.data.database.PexelsDatabase
import com.voitov.pexels_app.data.database.dao.FeaturedCollectionsDao
import com.voitov.pexels_app.data.database.dao.PhotosDao
import com.voitov.pexels_app.data.database.dao.UserPhotosDao
import com.voitov.pexels_app.data.database.entity.FeaturedCollectionsEntity
import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity
import com.voitov.pexels_app.data.datasource.cache.entity.PhotoDetailsCacheEntity
import com.voitov.pexels_app.data.datasource.local.PersistentKeyValueStorage
import com.voitov.pexels_app.data.mapper.FeaturedCollectionsMapper
import com.voitov.pexels_app.data.mapper.PhotosCacheMapper
import com.voitov.pexels_app.di.annotation.FeaturedCache
import com.voitov.pexels_app.di.annotation.PhotosCache
import com.voitov.pexels_app.domain.model.FeaturedCollection
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersistentCacheManager @Inject constructor(
    private val appDatabase: PexelsDatabase,
    private val bookmarkedPhotosDao: UserPhotosDao,
    private val featuredCollectionsCacheDao: FeaturedCollectionsDao,
    private val photosCacheDao: PhotosDao,
    private val scope: CoroutineScope,
    private val cacheConfig: PersistentKeyValueStorage<Long>,
    @PhotosCache
    private val inMemoryPhotoDetailsCache: HotCacheDataSource<Int, PhotoDetailsCacheEntity, String>,
    private val photoDetailsMapper: PhotosCacheMapper,
    @FeaturedCache
    private val inMemoryFeaturedCollectionsCache: HotCacheDataSource<String, FeaturedCollection, Nothing>,
    private val featuredCollectionsMapper: FeaturedCollectionsMapper,
    @ApplicationContext private val context: Context
) : CacheManager {
    private fun shouldInvalidate(currentTime: Long, cacheAge: Long): Boolean {
        return currentTime - cacheAge >= DATA_EXPIRATION_IN_SECONDS * 1000
    }

    private lateinit var job: Job

    override fun tryInvalidatingCache() {
        try {
            job = scope.launch {
                val currentTime = System.currentTimeMillis()
                val cacheAge = cacheConfig.getValue()

                if (cacheAge != null) {
                    val cachedPhotosEntities = photosCacheDao.getAllPhotos()
                    val cachePhotosForDeletion = cachedPhotosEntities.filter { cachedItem ->
                        !bookmarkedPhotosDao.isItemExists(cachedItem.id)
                    }

                    if (shouldInvalidate(currentTime, cacheAge)) {
                        appDatabase.withTransaction {
                            photosCacheDao.removeAll(cachePhotosForDeletion)
                            featuredCollectionsCacheDao.removeAll()
                            context.cacheDir.resolve(IMAGES_CACHE_FOLDER).deleteRecursively()
                            cacheConfig.put(currentTime)
                        }
                    }
                } else {
                    cacheConfig.put(currentTime)
                }
                initFeaturedCollectionsCache(featuredCollectionsCacheDao.getAll())
                initPhotoDetailsCache(photosCacheDao.getAllPhotos())
            }
        } catch (ex: Exception) {
            Log.d(TAG, ex.message.toString())
        }
    }

    override fun getImagesFolder(): String {
        return IMAGES_CACHE_FOLDER
    }

    override val cacheJob: Job
        get() = job

    private fun initFeaturedCollectionsCache(items: List<FeaturedCollectionsEntity>) {
        val domainModels = items.map {
            featuredCollectionsMapper.mapDbEntitiesToDomainModel(it)
        }
        inMemoryFeaturedCollectionsCache.updateCache(domainModels)
    }

    private fun initPhotoDetailsCache(items: List<PhotoDetailsEntity>) {
        photoDetailsMapper
        val cachedItems = items.map {
            photoDetailsMapper.mapDbEntityToCacheEntity(it)
        }
        inMemoryPhotoDetailsCache.updateCache(cachedItems)
    }

    companion object {
        private const val TAG = "PersistentCacheManager"
        const val IMAGES_CACHE_FOLDER = "image_cache"
        const val DATA_EXPIRATION_IN_SECONDS = 3600 // 1hour
    }
}