package com.voitov.pexels_app.data.datasource.cache.implementation

import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.cache.entity.PhotoDetailsCacheEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotosCacheImpl @Inject constructor() :
    HotCacheDataSource<Int, PhotoDetailsCacheEntity, String> {
    private val cache = LinkedHashMap<Int, PhotoDetailsCacheEntity>()

    @Synchronized
    override fun updateCache(items: List<PhotoDetailsCacheEntity>) {
        items.forEach {
            updateCache(it)
        }
    }

    @Synchronized
    override fun updateCache(item: PhotoDetailsCacheEntity) {
        cache[item.id] = item
    }

    @Synchronized
    override fun contains(id: Int): Boolean = cache.containsKey(id)

    @Synchronized
    override fun getAllCache(predicate: (String) -> Boolean): List<PhotoDetailsCacheEntity> {
        return cache.values.filter { predicate(it.query) }
    }

    @Synchronized
    override fun clear() {
        cache.clear()
    }

    @Synchronized
    override fun getItemById(id: Int): PhotoDetailsCacheEntity? {
        return cache[id]
    }
}