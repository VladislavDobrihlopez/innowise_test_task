package com.voitov.pexels_app.data.datasource.local

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.voitov.pexels_app.di.annotation.ApplicationScope
import com.voitov.pexels_app.domain.model.PhotoDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class LocalDataSourceImpl @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val context: Application,
) : LocalDataSource {
//    @OptIn(ExperimentalCoilApi::class)
//    override suspend fun tryToDownloadPhoto(photoDetails: PhotoDetails): Boolean {
//        return scope.async {
//            var inputStream: InputStream? = null
//            var outputStream: OutputStream? = null
//            try {
//                val directory =
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                Log.d("DOWNLOAD", context.imageLoader.diskCache!!.directory.name)
//                val cacheFile = File(context.imageLoader.diskCache!!.directory.name, photoDetails.sourceUrl)
//                val newFile = File(directory, "${photoDetails.id}.jpg")
//                outputStream = newFile.outputStream()
//                inputStream = cacheFile.inputStream()
//                val fileReader = ByteArray(1024 * 4)
//                var isRead = true
//                while (isRead) {
//                    ensureActive()
//                    val read = inputStream.read(fileReader)
//                    if (read == -1) {
//                        isRead = false
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(
//                                context,
//                                "Downloaded -> ${newFile.absolutePath}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } else {
//                        outputStream.write(fileReader, 0, read)
//                    }
//                }
//                true
//            } catch (e: IOException) {
//                Log.d("DOWNLOAD", e.message.toString())
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
//                }
//                false
//            } finally {
//                inputStream?.close()
//                outputStream?.close()
//            }
//        }.await()
//    }

    override suspend fun tryToDownloadPhoto(photoDetails: PhotoDetails): Boolean {
        return try {
            val dm =
                context.getSystemService(DownloadManager::class.java)
            val downloadUri = Uri.parse(photoDetails.sourceUrl)
            val request = DownloadManager.Request(downloadUri)
            request
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(photoDetails.author)
                .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
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