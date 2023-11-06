package com.voitov.pexels_app.data.datasource.cache.implementation

import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.domain.model.FeaturedCollection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeaturedCollectionsCacheImpl @Inject constructor() :
    HotCacheDataSource<String, FeaturedCollection, Nothing> {
    private val cache = LinkedHashMap<String, FeaturedCollection>()

    @Synchronized
    override fun getAllCache(predicate: (Nothing) -> Boolean): List<FeaturedCollection> {
        return cache.values.toList()
    }

    @Synchronized
    override fun getItemById(id: String): FeaturedCollection? {
        return cache[id]
    }

    @Synchronized
    override fun updateCache(items: List<FeaturedCollection>) {
        items.forEach {
            updateCache(it)
        }
    }

    @Synchronized
    override fun updateCache(item: FeaturedCollection) {
        cache[item.id] = item
    }

    @Synchronized
    override fun contains(id: String): Boolean {
        return cache.contains(id)
    }

    @Synchronized
    override fun clear() {
        cache.clear()
    }
}