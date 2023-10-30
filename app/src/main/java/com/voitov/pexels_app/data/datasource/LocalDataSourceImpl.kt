package com.voitov.pexels_app.data.datasource

import android.graphics.drawable.Drawable
import com.voitov.pexels_app.data.database.PhotoDetailsEntity
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(): LocalDataSource {
    override fun saveImageToCache(drawable: Drawable, entity: PhotoDetailsEntity) {
        TODO("Not yet implemented")
    }

    override fun saveImageToDownload(entity: PhotoDetailsEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getImage(photoId: Int): PhotoDetailsEntity {
        TODO("Not yet implemented")
    }
}