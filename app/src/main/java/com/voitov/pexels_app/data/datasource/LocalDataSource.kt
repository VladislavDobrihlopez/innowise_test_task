package com.voitov.pexels_app.data.datasource

import android.graphics.drawable.Drawable
import com.voitov.pexels_app.data.database.PhotoDetailsEntity

interface LocalDataSource {
    fun saveImageToCache(drawable: Drawable, entity: PhotoDetailsEntity)
    fun saveImageToDownload(entity: PhotoDetailsEntity)
    suspend fun getImage(photoId: Int): PhotoDetailsEntity
}