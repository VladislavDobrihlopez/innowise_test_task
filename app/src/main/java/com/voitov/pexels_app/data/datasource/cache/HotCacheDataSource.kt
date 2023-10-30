package com.voitov.pexels_app.data.datasource.cache

interface HotCacheDataSource<KEY_ID, CACHE_TYPE> {
    fun getValue(): List<CACHE_TYPE>
    fun updateCache(items: List<CACHE_TYPE>)
    fun updateCache(item: CACHE_TYPE)
    fun contains(id: KEY_ID): Boolean
    fun clear()
}