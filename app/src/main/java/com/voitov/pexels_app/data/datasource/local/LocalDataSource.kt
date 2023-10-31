package com.voitov.pexels_app.data.datasource.local

import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity

interface LocalDataSource {
    fun saveImageToDownload(entity: PhotoDetailsEntity)
}