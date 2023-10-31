package com.voitov.pexels_app.data.datasource.cache

interface HotCacheDataSource<KEY_ID, CACHE_TYPE, FILTER_KEY> {
    fun getAllCache(predicate: (FILTER_KEY) -> Boolean): List<CACHE_TYPE>
    fun getItemById(id: KEY_ID): CACHE_TYPE?
    fun updateCache(items: List<CACHE_TYPE>)
    fun updateCache(item: CACHE_TYPE)
    fun contains(id: KEY_ID): Boolean
    fun clear()
}