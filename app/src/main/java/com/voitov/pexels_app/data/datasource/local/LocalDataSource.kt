package com.voitov.pexels_app.data.datasource.local

import com.voitov.pexels_app.domain.model.PhotoDetails

interface LocalDataSource {
    fun tryToDownloadPhoto(photoDetails: PhotoDetails): Boolean
}