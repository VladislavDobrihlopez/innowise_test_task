package com.voitov.pexels_app.data.datasource.cache.impl

import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.cache.entity.PhotoDetailsCacheEntity
import javax.inject.Inject

class PhotosCacheImpl @Inject constructor() :
    HotCacheDataSource<Int, PhotoDetailsCacheEntity, String> {
    private val cache: HashMap<Int, PhotoDetailsCacheEntity> = HashMap()

    override fun updateCache(items: List<PhotoDetailsCacheEntity>) {
        items.forEach {
            updateCache(it)
        }
    }

    override fun updateCache(item: PhotoDetailsCacheEntity) {
        cache[item.id] = item
    }

    override fun contains(id: Int): Boolean = cache.containsKey(id)
    override fun getAllCache(predicate: (String) -> Boolean): List<PhotoDetailsCacheEntity> {
        return cache.values.toList().filter { predicate(it.query) }
    }

    override fun clear() {
        cache.clear()
    }

    override fun getItemById(id: Int): PhotoDetailsCacheEntity? {
        return cache[id]
    }
}