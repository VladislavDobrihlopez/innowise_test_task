package com.voitov.pexels_app.data.repository

import android.util.Log
import com.voitov.pexels_app.data.database.dao.FeaturedCollectionsDao
import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.remote.RemoteDataSource
import com.voitov.pexels_app.data.mapper.FeaturedCollectionsMapper
import com.voitov.pexels_app.data.repository.helper.FeaturedRequestBatch
import com.voitov.pexels_app.di.annotation.DispatcherIO
import com.voitov.pexels_app.di.annotation.FeaturedCache
import com.voitov.pexels_app.domain.model.FeaturedCollection
import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.domain.repository.PexelsFeaturedCollectionsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeaturedCollectionsRepositoryImpl @Inject constructor(
    private val dao: FeaturedCollectionsDao,
    private val remoteDataSource: RemoteDataSource,
    private val mapper: FeaturedCollectionsMapper,
    @FeaturedCache
    private val inMemoryHotCache: HotCacheDataSource<String, FeaturedCollection, Nothing>,
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope
) : PexelsFeaturedCollectionsRepository {
    private val refreshCollection = MutableSharedFlow<FeaturedRequestBatch>(replay = 1)

    init {
        initCache()
    }

    private fun initCache() {
        scope.launch {
            val dbEntities = dao.getAll()
            val domainModels = dbEntities.map {
                mapper.mapDbEntitiesToDomainModel(it)
            }
            inMemoryHotCache.updateCache(domainModels)
        }
    }

    override fun getFeaturedCollections() = flow<List<FeaturedCollection>> {
        refreshCollection.collect { batch ->
            try {
                val response = withContext(dispatcher) {
                    remoteDataSource.getFeaturedCollections(
                        page = batch.page,
                        batch = batch.pagesPerRequest
                    )
                }
                if (response.isSuccessful) {
                    val dtos = requireNotNull(response.body()?.collections)
                    val result = dtos.map { mapper.mapDtoToDomainModel(it) }

                    val itemsForCaching = result.filter { !inMemoryHotCache.contains(it.id) }
                    inMemoryHotCache.updateCache(itemsForCaching)

                    val dbEntities = dtos.map { mapper.mapDtoToDbEntity(it) }
                    dao.upsertAll(dbEntities)

                    emit(result)
                }
            } catch (ex: Exception) {
                Log.d(TAG, ex::class.toString())
            }

            val cacheEntities = inMemoryHotCache.getAllCache { true }
            emit(cacheEntities)
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(5000))

    override suspend fun requestFeaturedCollections(page: Int, batch: Int) {
        refreshCollection.emit(FeaturedRequestBatch(page = page, pagesPerRequest = batch))
    }

    companion object {
        private const val TAG = "PexelsFeaturedCollectionsRepositoryImpl"
    }
}