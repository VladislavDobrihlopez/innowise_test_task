package com.voitov.pexels_app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.voitov.pexels_app.data.datasource.cache.CacheManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PexelsApp : Application(), ImageLoaderFactory {
    @Inject
    lateinit var persistentCacheManager: CacheManager

    override fun onCreate() {
        super.onCreate()
        persistentCacheManager.setupCache()
    }

    override fun newImageLoader() =
        ImageLoader(this).newBuilder()
            .diskCache(
                DiskCache.Builder()
                    .directory(cacheDir.resolve(persistentCacheManager.getImagesFolder()))
                    .maxSizePercent(0.15)
                    .build()
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .logger(DebugLogger())
            .build()
}