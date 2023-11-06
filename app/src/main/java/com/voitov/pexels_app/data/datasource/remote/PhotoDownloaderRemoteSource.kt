package com.voitov.pexels_app.data.datasource.remote

import com.voitov.pexels_app.domain.model.PhotoDetails

interface PhotoDownloaderRemoteSource {
    fun tryToDownloadPhoto(photoDetails: PhotoDetails): Boolean
}