package com.voitov.pexels_app.data.datasource.cache

import com.voitov.pexels_app.data.database.PhotoDetailsEntity
import javax.inject.Inject

class PhotosFeedHotCacheDataSource @Inject constructor() :
    HotCacheDataSource<Int, PhotoDetailsEntity> {
    private val cache: HashMap<Int, PhotoDetailsEntity> = HashMap()

    override fun getValue() = cache.values.toList()

    override fun updateCache(items: List<PhotoDetailsEntity>) {
        cache.putAll(items.map { it.id to it })
    }

    override fun updateCache(item: PhotoDetailsEntity) {
        cache[item.id] = item
    }

    override fun contains(id: Int): Boolean = cache.containsKey(id)

    override fun clear() {
        cache.clear()
    }
}