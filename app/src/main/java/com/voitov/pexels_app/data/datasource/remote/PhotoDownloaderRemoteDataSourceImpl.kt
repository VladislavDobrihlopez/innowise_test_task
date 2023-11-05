package com.voitov.pexels_app.data.datasource.remote

import android.app.Application
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import com.voitov.pexels_app.domain.model.PhotoDetails
import java.io.File
import javax.inject.Inject


class PhotoDownloaderRemoteDataSourceImpl @Inject constructor(
    private val context: Application,
) : PhotoDownloaderRemoteSource {
    override fun tryToDownloadPhoto(photoDetails: PhotoDetails): Boolean {
        return try {
            val dm = context.getSystemService(DownloadManager::class.java)
            val downloadUri = Uri.parse(photoDetails.sourceUrl)
            val request = DownloadManager.Request(downloadUri)
            request
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(photoDetails.author)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator + "${photoDetails.id}_${photoDetails.author}" + ".jpg"
                )
            dm.enqueue(request)
            true
        } catch (e: Exception) {
            false
        }
    }
}