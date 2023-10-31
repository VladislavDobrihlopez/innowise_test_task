package com.voitov.pexels_app.data.datasource.local

import android.app.Application
import android.graphics.drawable.Drawable
import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity
import com.voitov.pexels_app.di.annotation.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val context: Application,
): LocalDataSource {
    override fun saveImageToDownload(entity: PhotoDetailsEntity) {
        TODO("Not yet implemented")
    }
}