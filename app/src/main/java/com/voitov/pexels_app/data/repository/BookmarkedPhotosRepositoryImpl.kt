package com.voitov.pexels_app.data.repository

import com.voitov.pexels_app.data.database.dao.UserPhotosDao
import com.voitov.pexels_app.data.mapper.BookmarkedPhotosMapper
import com.voitov.pexels_app.di.annotation.DispatcherIO
import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.domain.repository.PexelsBookmarkedPhotosRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookmarkedPhotosRepositoryImpl @Inject constructor(
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val dao: UserPhotosDao,
    private val mapper: BookmarkedPhotosMapper
) : PexelsBookmarkedPhotosRepository {
    override fun getAllBookmarkedPhotos(): Flow<List<PhotoDetails>> {
        return dao.getAllBookmarkedPhotos().map { dbEntities ->
            dbEntities.map { mapper.mapDbEntityToDomain(it) }
        }
    }

    override suspend fun insertPhoto(item: PhotoDetails, query: String) {
        withContext(dispatcher) {
            dao.insertPhoto(mapper.mapDomainToDbEntity(item, query))
        }
    }

    override suspend fun deletePhotoById(id: Int) {
        withContext(dispatcher) {
            dao.deletePhotoById(id)
        }
    }
}