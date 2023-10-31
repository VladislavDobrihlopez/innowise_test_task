package com.voitov.pexels_app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PexelsApp : Application(), ImageLoaderFactory {
    override fun newImageLoader() =
        ImageLoader(this).newBuilder()
            .diskCache(
                DiskCache.Builder()
                    .directory(cacheDir)
                    .maxSizePercent(0.05)
                    .build()
            )
            .memoryCache(
                MemoryCache.Builder(this)
                    .weakReferencesEnabled(true)
                    .maxSizePercent(0.15)
                    .build()
            )
            .logger(DebugLogger())
            .build()
}