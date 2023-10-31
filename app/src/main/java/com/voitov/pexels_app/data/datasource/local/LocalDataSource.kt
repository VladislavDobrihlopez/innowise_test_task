package com.voitov.pexels_app.data.datasource.local

import com.voitov.pexels_app.domain.model.PhotoDetails

interface LocalDataSource {
    suspend fun tryToDownloadPhoto(photoDetails: PhotoDetails): Boolean
}