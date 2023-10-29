package com.voitov.pexels_app.data.repository

import android.util.Log
import com.voitov.pexels_app.data.datasource.LocalDataSource
import com.voitov.pexels_app.data.datasource.RemoteDataSource
import com.voitov.pexels_app.data.mapper.PexelsMapper
import com.voitov.pexels_app.data.network.dto.photo.PhotoDto
import com.voitov.pexels_app.domain.PexelsException
import com.voitov.pexels_app.domain.PexelsRepository
import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class PexelsRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val mapper: PexelsMapper,
) : PexelsRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val refreshCollection = MutableSharedFlow<Unit>(replay = 1)

    override fun getFeaturedCollections() = flow<List<FeaturedCollection>> {
        refreshCollection.collect {
            try {
                val response = remoteDataSource.getFeaturedCollections()
                val result = response.collections.map { mapper.mapDtoToDomainModel(it) }
                emit(result)
            } catch (ex: Exception) {
                if (ex is HttpException) {
                    throw PexelsException.NoInternet
                } else {
                    throw PexelsException.UnexpectedError
                }
            }
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(5000))

    private val refreshPhotos =
        MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getCuratedPhotos() = flow<List<Photo>> {
        refreshPhotos.collect { query ->
            try {
                val photosDto = retrievePhotos(query)
                val mappedPhotos = photosDto.map { mapper.mapDtoToDomainModel(it) }
                emit(mappedPhotos)
            } catch (ex: Exception) {
                if (ex is HttpException) {
                    throw PexelsException.NoInternet
                } else {
                    throw PexelsException.UnexpectedError
                }
            }
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(5000))

    private suspend fun retrievePhotos(query: String): List<PhotoDto> {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "requestPhotos: in")
            val response =
                if (query.isEmpty()) {
                    remoteDataSource.getCuratedPhotos()
                } else {
                    remoteDataSource.searchForPhotos(query)
                }

            Log.d(TAG, "requestPhotos out: ${response.photos}")
            response.photos
        }
    }

    override suspend fun requestPhotos(query: String) {
        refreshPhotos.emit(query)
    }

    override suspend fun requestFeaturedCollections() {
        refreshCollection.emit(Unit)
    }

    companion object {
        private const val TAG = "PexelsRepositoryImpl"
    }
}