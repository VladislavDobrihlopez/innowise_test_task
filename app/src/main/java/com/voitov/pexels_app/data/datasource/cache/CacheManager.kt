package com.voitov.pexels_app.data.datasource.cache

import kotlinx.coroutines.Job

interface CacheManager {
    fun tryInvalidatingCache()
    fun getImagesFolder(): String
    val cacheJob: Job
}