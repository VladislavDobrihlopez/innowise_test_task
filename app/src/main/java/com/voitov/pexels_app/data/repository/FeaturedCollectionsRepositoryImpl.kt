package com.voitov.pexels_app.data.repository

import android.util.Log
import com.voitov.pexels_app.data.database.dao.FeaturedCollectionsDao
import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.remote.RemoteDataSource
import com.voitov.pexels_app.data.mapper.FeaturedCollectionsMapper
import com.voitov.pexels_app.di.annotation.DispatcherIO
import com.voitov.pexels_app.di.annotation.FeaturedCache
import com.voitov.pexels_app.domain.RequestBatch
import com.voitov.pexels_app.domain.model.FeaturedCollection
import com.voitov.pexels_app.domain.repository.PexelsFeaturedCollectionsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeaturedCollectionsRepositoryImpl @Inject constructor(
    private val collectionsDatabase: FeaturedCollectionsDao,
    private val remoteDataSource: RemoteDataSource,
    private val collectionsMapper: FeaturedCollectionsMapper,
    @FeaturedCache
    private val memoryCache: HotCacheDataSource<String, FeaturedCollection, Nothing>,
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope
) : PexelsFeaturedCollectionsRepository {
    private val refreshCollection = MutableSharedFlow<RequestBatch>(replay = 1)

    private lateinit var initJob: Job

    init {
        initCache()
    }

    private fun initCache() {
        initJob = scope.launch {
            val dbEntities = collectionsDatabase.getAll()
            val domainModels = dbEntities.map {
                collectionsMapper.mapDbEntitiesToDomainModel(it)
            }
            memoryCache.updateCache(domainModels)
        }
    }

    override fun getFeaturedCollections() = flow<List<FeaturedCollection>> {
        refreshCollection.collect { batch ->
            initJob.join()
            try {
                val oldCache = memoryCache.getAllCache { true }

                if (oldCache.isNotEmpty()) {
                    emit(oldCache)
                }

                val response = withContext(dispatcher) {
                    remoteDataSource.getFeaturedCollections(
                        page = batch.page,
                        batch = batch.pagesPerRequest
                    )
                }
                if (response.isSuccessful) {
                    val dtos = requireNotNull(response.body()?.collections)
                    val result = dtos.map { collectionsMapper.mapDtoToDomainModel(it) }

                    val itemsForCaching = result.filter { !memoryCache.contains(it.id) }
                    memoryCache.updateCache(itemsForCaching)

                    val dbEntities = dtos.map { collectionsMapper.mapDtoToDbEntity(it) }
                    collectionsDatabase.upsertAll(dbEntities)
                }
            } catch (ex: Exception) {
                Log.d(TAG, ex::class.toString())
            }

            val cacheEntities = memoryCache.getAllCache { true }
            emit(cacheEntities)
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed())

    override suspend fun requestFeaturedCollections(page: Int, batch: Int) {
        refreshCollection.emit(RequestBatch(page = page, pagesPerRequest = batch))
    }

    companion object {
        private const val TAG = "PexelsFeaturedCollectionsRepositoryImpl"
    }
}