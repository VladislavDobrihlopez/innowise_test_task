package com.voitov.pexels_app.data.datasource.local

import android.app.Application
import android.graphics.drawable.Drawable
import com.voitov.pexels_app.data.database.PhotoDetailsEntity
import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val context: Application,
): LocalDataSource {
    override fun saveImageToCache(drawable: Drawable, entity: PhotoDetailsEntity) {
        scope.launch {
//            val bitmap = (drawable as BitmapDrawable).bitmap
//            val contextWrapper = ContextWrapper(context)
//            val directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)
//            val file = File(directory, "${catEntity.id}.jpg")
//            val fos = FileOutputStream(file)
//            try {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//                fos.flush()
//                database.updateCat(
//                    entity.copy(
//                        localUrl = file.absolutePath,
//                        isDownload = true
//                    )
//                )
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } finally {
//                fos.close()
//            }

        }
    }

    override fun saveImageToDownload(entity: PhotoDetailsEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getImage(photoId: Int): PhotoDetailsEntity {
        TODO("Not yet implemented")
    }
}