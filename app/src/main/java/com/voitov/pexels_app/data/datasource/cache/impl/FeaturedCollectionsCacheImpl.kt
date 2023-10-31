package com.voitov.pexels_app.data.datasource.cache.impl

import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.domain.model.FeaturedCollection
import javax.inject.Inject

class FeaturedCollectionsCacheImpl @Inject constructor() :
    HotCacheDataSource<String, FeaturedCollection, Nothing> {
    private val cache = HashMap<String, FeaturedCollection>()
    override fun getAllCache(predicate: (Nothing) -> Boolean): List<FeaturedCollection> {
        return cache.values.toList()
    }

    override fun getItemById(id: String): FeaturedCollection? {
        return cache[id]
    }

    override fun updateCache(items: List<FeaturedCollection>) {
        items.forEach {
            updateCache(it)
        }
    }

    override fun updateCache(item: FeaturedCollection) {
        cache[item.id] = item
    }

    override fun contains(id: String): Boolean {
        return cache.contains(id)
    }

    override fun clear() {
        cache.clear()
    }
}